---
name: test-cli
description: Test this project's Java CLI from the ordered cases in tests/test-plan.md, compile the current src/main/java code, and report each input followed by the executable's actual terminal-style output. Use when validating CLI behavior, checking positive and negative cases, or producing a reproducible CLI test record.
---

# Test CLI

All future Java source and test code for this project must follow the
`se-edu-java-coding-standard` skill. Apply its basic and intermediate rules by
default; use its advanced rules only when explicitly requested.

When creating or proposing a commit message for this project, follow the
`craft-commit-message` skill. Every commit message must include an imperative
subject and a detailed body explaining what changed and why.

Run the current CLI against the ordered cases in `tests/test-plan.md` and show
a reproducible input/output record. The plan is the source of truth for the
inputs and per-case output assertions. Treat the executable output as
authoritative; do not invent or replace it with an expected transcript. Check
each response immediately and stop before sending any later input when a case
fails.

## Java runtime

This project targets JDK 25. Before running the Gradle or CLI tests, select the
project's configured JDK 25 candidate in the current shell:

```bash
sdk use java 25.0.3.fx-zulu
java -version
javac -version
```

The `sdk use` selection applies only to the current shell, so repeat it when
starting a new shell for testing.

## Workflow

1. Work from the project root and read `tests/test-plan.md`. Use its test-case
   table as the complete input sequence and expected-output checklist. Read
   `docs/README.md` only as supporting user documentation.
2. Read the current files under `src/main/java` to identify the entry point and
   current build assumptions. For this project, the entry point is `Damien`.
3. Extract every input and its expected output fragments from the ordered test
   case table. Run the cases exactly in that order, including both positive and
   negative cases. Do not derive a shorter sequence from `docs/README.md` or
   move all negative cases to the end.
4. Run the bundled script:

   ```bash
   python3 .codex/skills/test-cli/scripts/run_cli_tests.py
   ```

   The script compiles all current Java sources into a temporary directory,
   runs the real executable with the sequence from `tests/test-plan.md`, checks
   the startup greeting and each response against that case's output fragments
   as soon as it is produced, and prints the transcript. On the first failed
   case, it stops the CLI session and prints that case's expected output
   fragments followed by the actual output for comparison.
5. Report the test inputs in order. Prefix each input with `>` and place the
   actual output emitted for that input immediately below it. If a case fails,
   report no later inputs, then include the expected and actual output shown by
   the script. Include the final `PASS` or failure details from the script.

## Reporting rules

- Preserve output wording, spacing, task numbering, and separator lines.
- Distinguish input annotations from executable output; `>` is report framing,
  not output produced by Damien.
- Stop at the first failed test case. Do not send or report subsequent cases.
- For a failed case, report every expected output fragment from the plan and the
  complete actual response block produced by Damien.
- If compilation fails, report the compiler output and do not claim a CLI test
  passed.
- If a documented example cannot be extracted or the project entry point has
  changed, inspect the current code and adapt the runner before testing.
