# Damien test plan

## Purpose

This plan describes the end-to-end CLI test run performed by an AI. It covers
the normal commands documented in `docs/README.md`, malformed commands, and
invalid task numbers. The cases are run in one session so that negative cases
also verify that an error does not change the task list or later task state.

## Test method

The AI runs the test from the project root with:

```bash
python3 .codex/skills/test-cli/scripts/run_cli_tests.py
```

The runner:

1. Reads the ordered test-case table below from this file.
2. Compiles every Java source file under `src/main/java` with
   `javac -Xlint:all` into a temporary classes directory.
3. Starts `Damien` and sends the inputs below, one line at a time, in a
   single process.
4. Prints the actual output next to the input that produced it.
5. Checks each response against that test case's expected output fragments,
   checks the startup greeting, and requires a normal exit.

The executable's output is authoritative. The expected output fragments below
are assertions used to detect regressions; the AI reports the actual wording,
spacing, separators, task numbering, and status markers printed by Damien.

## Test cases in execution order

The startup greeting is checked before the first input. The following cases
are deliberately interleaved: valid commands are followed by invalid commands
and later `list` commands verify that failed commands did not alter the state.
Every code span in the last column is an output substring that must occur in
the response for that row.

| Order | Type | Input | Expected output fragments |
| --- | --- | --- | --- |
| 1 | Positive | `list` | `Here are the tasks in your list:`; no task is listed yet. |
| 2 | Negative | `what is this` | ` OOPS!!! I'm sorry, but I don't know what that means :-(` |
| 3 | Positive | `todo borrow book` | `[T][ ] borrow book`; `Now you have 1 tasks in the list.` |
| 4 | Negative | `todo` | ` OOPS!!! The description of a todo cannot be empty.` |
| 5 | Positive state check | `list` | `1.[T][ ] borrow book` |
| 6 | Negative | `deadline` | ` OOPS!!! A deadline needs a /by field, for example: deadline return book /by Sunday.` |
| 7 | Positive | `deadline return book /by Sunday` | `[D][ ] return book (by: Sunday)`; `Now you have 2 tasks in the list.` |
| 8 | Negative | `deadline return book` | ` OOPS!!! A deadline needs a /by field, for example: deadline return book /by Sunday.` |
| 9 | Negative | `deadline /by Sunday` | ` OOPS!!! A deadline needs a description before /by, for example: deadline return book /by Sunday.` |
| 10 | Negative | `deadline return book /by` | ` OOPS!!! A deadline needs a date or time after /by, for example: deadline return book /by Sunday.` |
| 11 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Sunday)` |
| 12 | Negative | `event` | ` OOPS!!! An event needs a /from field, for example: event meeting /from 2pm /to 4pm.` |
| 13 | Positive | `event project meeting /from Mon 2pm /to 4pm` | `[E][ ] project meeting (from: Mon 2pm to: 4pm)`; `Now you have 3 tasks in the list.` |
| 14 | Negative | `event project meeting /to 4pm` | ` OOPS!!! An event needs a /from field, for example: event meeting /from 2pm /to 4pm.` |
| 15 | Negative | `event project meeting /from Mon 2pm` | ` OOPS!!! An event needs a /to field, for example: event meeting /from 2pm /to 4pm.` |
| 16 | Negative | `event /from Mon 2pm /to 4pm` | ` OOPS!!! An event needs a description before /from, for example: event meeting /from 2pm /to 4pm.` |
| 17 | Negative | `event project meeting /from /to 4pm` | ` OOPS!!! An event needs a start time after /from, for example: event meeting /from 2pm /to 4pm.` |
| 18 | Negative | `event project meeting /from Mon 2pm /to` | ` OOPS!!! An event needs an end time after /to, for example: event meeting /from 2pm /to 4pm.` |
| 19 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Sunday)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 20 | Negative | `mark` | ` OOPS!!! Please provide a task number after mark, for example: mark 1.` |
| 21 | Negative | `mark abc` | ` OOPS!!! The task number after mark must be a positive integer, for example: mark 1.` |
| 22 | Negative | `mark 0` | ` OOPS!!! Task numbers start at 1. Use list to see valid task numbers.` |
| 23 | Negative | `mark 4` | ` OOPS!!! Task 4 does not exist. Use list to see valid task numbers.` |
| 24 | Positive | `mark 1` | `[T][X] borrow book`; `Nice! I've marked this task as done:` |
| 25 | Positive state check | `list` | `1.[T][X] borrow book`; `2.[D][ ] return book (by: Sunday)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 26 | Negative | `unmark` | ` OOPS!!! Please provide a task number after unmark, for example: unmark 1.` |
| 27 | Negative | `unmark abc` | ` OOPS!!! The task number after unmark must be a positive integer, for example: unmark 1.` |
| 28 | Negative | `unmark 0` | ` OOPS!!! Task numbers start at 1. Use list to see valid task numbers.` |
| 29 | Negative | `unmark 4` | ` OOPS!!! Task 4 does not exist. Use list to see valid task numbers.` |
| 30 | Positive | `unmark 1` | `[T][ ] borrow book`; `OK, I've marked this task as not done yet:` |
| 31 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Sunday)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 32 | Positive termination | `bye` | `Bye. Hope to see you again soon!` |

## Pass criteria

The script reports `PASS` only when compilation succeeds, the startup greeting
is present, every response contains all of its expected output fragments,
Damien exits with status 0, and all 32 inputs complete. Any compiler, runtime,
or missing-output failure means that the test run did not pass.
