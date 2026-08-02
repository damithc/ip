#!/usr/bin/env python3
"""Run the ordered Damien CLI cases from tests/test-plan.md."""

from __future__ import annotations

from dataclasses import dataclass
import re
import subprocess
import sys
import tempfile
from pathlib import Path


SEPARATOR = "____________________________________________________________"
TEST_CASES_HEADING = "## Test cases in execution order"
PERSISTENCE_CASES_HEADING = "## Persistence test cases in execution order"
CORRUPTION_CASES_HEADING = "## Corrupted data test cases"
CODE_SPAN_PATTERN = re.compile(r"`([^`]+)`")
STARTUP_FRAGMENTS = ("Hello! I'm Damien", "What can I do for you?")
CORRUPTED_DATA = "\n".join(
    (
        "T | 0 | keep this task",
        "corrupted record",
        "D | 0 | return book | Sunday",
        "E | invalid | project meeting | Mon 2pm | 4pm",
    )
) + "\n"


@dataclass(frozen=True)
class TestCase:
    """One input and the output fragments expected for that input."""

    order: int
    command: str
    expected_fragments: tuple[str, ...]
    session: int = 0


def find_project_root() -> Path:
    """Find the project root containing the test plan and Java sources."""
    for candidate in (Path.cwd(), *Path.cwd().parents):
        if (candidate / "tests" / "test-plan.md").is_file() and (
            candidate / "src" / "main" / "java"
        ).is_dir():
            return candidate
    raise SystemExit(
        "Could not find project root containing tests/test-plan.md and src/main/java"
    )


def test_cases_from_plan(plan: str) -> list[TestCase]:
    """Extract the ordered test-case table from the Markdown test plan."""
    section_pattern = re.compile(
        rf"^{re.escape(TEST_CASES_HEADING)}\s*$" r"(.*?)(?=^## |\Z)",
        re.DOTALL | re.MULTILINE,
    )
    section_match = section_pattern.search(plan)
    if section_match is None:
        raise SystemExit(f"Could not find '{TEST_CASES_HEADING}' in tests/test-plan.md")

    test_cases: list[TestCase] = []
    for line in section_match.group(1).splitlines():
        stripped_line = line.strip()
        if not stripped_line.startswith("|") or stripped_line.startswith("| ---"):
            continue

        cells = [cell.strip() for cell in stripped_line.strip("|").split("|")]
        if len(cells) != 4 or cells[0] == "Order":
            continue

        try:
            order = int(cells[0])
        except ValueError as exception:
            raise SystemExit(f"Invalid test-case order in tests/test-plan.md: {line}") from exception

        commands = CODE_SPAN_PATTERN.findall(cells[2])
        expected_fragments = tuple(CODE_SPAN_PATTERN.findall(cells[3]))
        if len(commands) != 1 or not expected_fragments:
            raise SystemExit(
                "Each test case must contain one input code span and at least "
                f"one expected-output code span: {line}"
            )
        test_cases.append(TestCase(order, commands[0], expected_fragments))

    expected_orders = list(range(1, len(test_cases) + 1))
    actual_orders = [test_case.order for test_case in test_cases]
    if actual_orders != expected_orders:
        raise SystemExit(
            "Test cases in tests/test-plan.md must be numbered consecutively "
            f"from 1: found {actual_orders}"
        )
    if not test_cases:
        raise SystemExit("No test cases found in tests/test-plan.md")
    if test_cases[-1].command != "bye":
        raise SystemExit("The final test case in tests/test-plan.md must be 'bye'")
    return test_cases


def persistence_cases_from_plan(plan: str) -> list[TestCase]:
    """Extract the ordered multi-process persistence cases from the plan."""
    section_pattern = re.compile(
        rf"^{re.escape(PERSISTENCE_CASES_HEADING)}\s*$" r"(.*?)(?=^## |\Z)",
        re.DOTALL | re.MULTILINE,
    )
    section_match = section_pattern.search(plan)
    if section_match is None:
        raise SystemExit(
            f"Could not find '{PERSISTENCE_CASES_HEADING}' in tests/test-plan.md"
        )

    test_cases: list[TestCase] = []
    for line in section_match.group(1).splitlines():
        stripped_line = line.strip()
        if not stripped_line.startswith("|") or stripped_line.startswith("| ---"):
            continue

        cells = [cell.strip() for cell in stripped_line.strip("|").split("|")]
        if len(cells) != 4 or cells[0] == "Session":
            continue

        try:
            session = int(cells[0])
            order = int(cells[1])
        except ValueError as exception:
            raise SystemExit(
                f"Invalid persistence test order in tests/test-plan.md: {line}"
            ) from exception

        commands = CODE_SPAN_PATTERN.findall(cells[2])
        expected_fragments = tuple(CODE_SPAN_PATTERN.findall(cells[3]))
        if len(commands) != 1 or not expected_fragments:
            raise SystemExit(
                "Each persistence test case must contain one input code span and "
                f"at least one expected-output code span: {line}"
            )
        test_cases.append(TestCase(order, commands[0], expected_fragments, session))

    if not test_cases:
        raise SystemExit("No persistence test cases found in tests/test-plan.md")

    sessions = sorted({test_case.session for test_case in test_cases})
    expected_sessions = list(range(1, len(sessions) + 1))
    if sessions != expected_sessions:
        raise SystemExit(
            "Persistence test sessions in tests/test-plan.md must be numbered "
            f"consecutively from 1: found {sessions}"
        )

    for session in sessions:
        session_cases = [case for case in test_cases if case.session == session]
        actual_orders = [test_case.order for test_case in session_cases]
        expected_orders = list(range(1, len(session_cases) + 1))
        if actual_orders != expected_orders:
            raise SystemExit(
                "Persistence test cases in each session must be numbered "
                f"consecutively from 1: found {actual_orders} in session {session}"
            )
        if session_cases[-1].command != "bye":
            raise SystemExit(
                f"The final persistence case in session {session} must be 'bye'"
            )
    return test_cases


def corruption_cases_from_plan(plan: str) -> tuple[list[TestCase], str]:
    """Extract the corrupted-data cases and startup warning from the plan."""
    section_pattern = re.compile(
        rf"^{re.escape(CORRUPTION_CASES_HEADING)}\s*$" r"(.*?)(?=^## |\Z)",
        re.DOTALL | re.MULTILINE,
    )
    section_match = section_pattern.search(plan)
    if section_match is None:
        raise SystemExit(
            f"Could not find '{CORRUPTION_CASES_HEADING}' in tests/test-plan.md"
        )

    section = section_match.group(1)
    warning_match = re.search(r"The startup output must contain `([^`]+)`", section)
    if warning_match is None:
        raise SystemExit(
            "The corrupted-data test section must specify its startup warning"
        )

    test_cases: list[TestCase] = []
    for line in section.splitlines():
        stripped_line = line.strip()
        if not stripped_line.startswith("|") or stripped_line.startswith("| ---"):
            continue

        cells = [cell.strip() for cell in stripped_line.strip("|").split("|")]
        if len(cells) != 3 or cells[0] == "Order":
            continue

        try:
            order = int(cells[0])
        except ValueError as exception:
            raise SystemExit(
                f"Invalid corrupted-data test order in tests/test-plan.md: {line}"
            ) from exception

        commands = CODE_SPAN_PATTERN.findall(cells[1])
        expected_fragments = tuple(CODE_SPAN_PATTERN.findall(cells[2]))
        if len(commands) != 1 or not expected_fragments:
            raise SystemExit(
                "Each corrupted-data test case must contain one input code span "
                f"and at least one expected-output code span: {line}"
            )
        test_cases.append(TestCase(order, commands[0], expected_fragments))

    actual_orders = [test_case.order for test_case in test_cases]
    if actual_orders != list(range(1, len(test_cases) + 1)):
        raise SystemExit(
            "Corrupted-data test cases in tests/test-plan.md must be numbered "
            f"consecutively from 1: found {actual_orders}"
        )
    if not test_cases or test_cases[-1].command != "bye":
        raise SystemExit(
            "The final corrupted-data test case in tests/test-plan.md must be 'bye'"
        )
    return test_cases, warning_match.group(1)


def compile_sources(root: Path, classes: Path) -> None:
    """Compile the current sources into the supplied temporary directory."""
    source_files = sorted((root / "src" / "main" / "java").rglob("*.java"))
    if not source_files:
        raise SystemExit("No Java source files found under src/main/java")

    compile_result = subprocess.run(
        ["javac", "-Xlint:all", "-d", str(classes), *map(str, source_files)],
        cwd=root,
        text=True,
        capture_output=True,
        check=False,
    )
    if compile_result.returncode != 0:
        print("Compilation failed:", file=sys.stderr)
        print(compile_result.stdout, end="", file=sys.stderr)
        print(compile_result.stderr, end="", file=sys.stderr)
        raise SystemExit(compile_result.returncode)


def read_output_block(stream) -> tuple[str, bool]:
    """Read one startup or response block, ending at its second separator."""
    lines: list[str] = []
    separator_count = 0
    while True:
        line = stream.readline()
        if line == "":
            return "\n".join(lines), False

        line = line.rstrip("\n")
        lines.append(line)
        if line == SEPARATOR:
            separator_count += 1
            if separator_count == 2:
                return "\n".join(lines), True


def stop_process(process: subprocess.Popen[str]) -> None:
    """Stop a CLI process that is waiting for more input."""
    if process.stdin is not None and not process.stdin.closed:
        process.stdin.close()
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=2)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait()


def print_expected_actual(test_case: TestCase, actual: str) -> None:
    """Print the failed case's expected fragments and actual output."""
    case_label = f"session {test_case.session}, case {test_case.order}" \
        if test_case.session else f"case {test_case.order}"
    print(
        f"FAIL: {case_label} ({test_case.command!r}) failed; "
        "stopping immediately."
    )
    print("Expected output fragments:")
    for fragment in test_case.expected_fragments:
        print(f"  {fragment}")
    print("Actual output:")
    print(actual if actual else "  (no output)")


def start_process(classes: Path, runtime: Path) -> subprocess.Popen[str]:
    """Start Damien in the supplied isolated runtime directory."""
    return subprocess.Popen(
        ["java", "-cp", str(classes), "duke.Damien"],
        cwd=runtime,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1,
    )


def check_startup(
    process: subprocess.Popen[str],
    label: str,
    additional_fragments: tuple[str, ...] = (),
) -> bool:
    """Read and validate one process's startup greeting."""
    if process.stdout is None or process.stdin is None:
        raise SystemExit("Could not open Damien's standard streams")

    expected_fragments = STARTUP_FRAGMENTS + additional_fragments
    startup, startup_complete = read_output_block(process.stdout)
    print(f"{label} startup output:")
    print(startup)
    if not startup_complete:
        print("FAIL: incomplete startup output; stopping immediately.")
        print("Expected startup fragments:")
        for fragment in expected_fragments:
            print(f"  {fragment}")
        print("Actual output:")
        print(startup if startup else "  (no output)")
        return False

    missing_startup = [fragment for fragment in expected_fragments if fragment not in startup]
    if missing_startup:
        print("FAIL: startup output did not match; stopping immediately.")
        print("Expected startup fragments:")
        for fragment in expected_fragments:
            print(f"  {fragment}")
        print("Actual output:")
        print(startup)
        return False
    return True


def run_session(
    classes: Path,
    runtime: Path,
    test_cases: list[TestCase],
    startup_label: str,
    startup_fragments: tuple[str, ...] = (),
) -> int:
    """Run and check one Damien process session."""
    process = start_process(classes, runtime)
    try:
        if not check_startup(process, startup_label, startup_fragments):
            return 1

        if process.stdout is None or process.stdin is None:
            raise SystemExit("Could not open Damien's standard streams")

        for test_case in test_cases:
            print(f"> {test_case.command}")
            process.stdin.write(test_case.command + "\n")
            process.stdin.flush()
            actual, complete = read_output_block(process.stdout)
            print(actual)

            if not complete:
                print_expected_actual(test_case, actual)
                print("FAIL: Damien ended before the response was complete.")
                return 1

            missing_fragments = [
                fragment
                for fragment in test_case.expected_fragments
                if fragment not in actual
            ]
            if missing_fragments:
                print_expected_actual(test_case, actual)
                return 1

        process.stdin.close()
        return_code = process.wait()
        if process.stderr is not None:
            error_output = process.stderr.read()
            if error_output:
                print(error_output, end="", file=sys.stderr)
        if return_code != 0:
            print(f"FAIL: Damien exited with status {return_code}")
            return return_code
        return 0
    finally:
        stop_process(process)


def run_cli(
    root: Path,
    test_cases: list[TestCase],
    persistence_cases: list[TestCase],
    corruption_cases: list[TestCase],
    corruption_warning: str,
) -> int:
    """Compile Damien and check all normal, persistence, and corruption sessions."""
    with tempfile.TemporaryDirectory(prefix="test-cli-") as temporary_directory:
        temporary_root = Path(temporary_directory)
        classes = temporary_root / "classes"
        runtime = temporary_root / "runtime"
        classes.mkdir()
        runtime.mkdir()
        compile_sources(root, classes)

        result = run_session(classes, runtime, test_cases, "Main session")
        if result != 0:
            return result

        for session in sorted({test_case.session for test_case in persistence_cases}):
            session_cases = [
                test_case for test_case in persistence_cases if test_case.session == session
            ]
            result = run_session(
                classes,
                runtime,
                session_cases,
                f"Persistence session {session}",
            )
            if result != 0:
                return result

        corrupted_runtime = temporary_root / "corrupted-runtime"
        corrupted_data_directory = corrupted_runtime / "data"
        corrupted_data_directory.mkdir(parents=True)
        (corrupted_data_directory / "duke.txt").write_text(
            CORRUPTED_DATA,
            encoding="utf-8",
        )
        result = run_session(
            classes,
            corrupted_runtime,
            corruption_cases,
            "Corrupted-data session",
            (corruption_warning,),
        )
        if result != 0:
            return result

        print(
            f"PASS: {len(test_cases)} main-session inputs and "
            f"{len(persistence_cases)} persistence-session inputs completed "
            f"and {len(corruption_cases)} corrupted-data inputs completed across "
            f"{2 + len({test_case.session for test_case in persistence_cases})} sessions"
        )
        return 0


def main() -> int:
    root = find_project_root()
    plan = (root / "tests" / "test-plan.md").read_text(encoding="utf-8")
    test_cases = test_cases_from_plan(plan)
    persistence_cases = persistence_cases_from_plan(plan)
    corruption_cases, corruption_warning = corruption_cases_from_plan(plan)
    commands = [test_case.command for test_case in test_cases]

    print(f"Project: {root}")
    print(
        f"Inputs: {len(commands)} main + {len(persistence_cases)} persistence "
        f"+ {len(corruption_cases)} corrupted-data"
    )
    return run_cli(
        root,
        test_cases,
        persistence_cases,
        corruption_cases,
        corruption_warning,
    )


if __name__ == "__main__":
    raise SystemExit(main())
