# Damien test plan

## Purpose

This plan describes the end-to-end command-processing test run performed by an
AI. It covers
the normal commands documented in `docs/README.md`, malformed commands, and
invalid task numbers. The cases are run in one session so that negative cases
also verify that an error does not change the task list or later task state.
The GUI uses the same command-processing path, so these cases remain useful
regression coverage even though the CLI is deprecated.

## Test method

The AI runs the JUnit and JavaFX-aware compilation tests from the project root
with:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew test
```

For the end-to-end CLI session represented by the table, use the deprecated
Gradle task below and provide the inputs in order (or redirect a prepared input
file to it):

```bash
./gradlew runCli
```

The Gradle test task:

1. Compiles the CLI and JavaFX sources with the JavaFX dependencies declared in
   `build.gradle`.
2. Runs the JUnit tests, including command-processing coverage used by the
   GUI.

The executable's output remains authoritative for the end-to-end CLI run. The
expected output fragments below are assertions used to detect regressions;
compare the actual wording, spacing, separators, task numbering, and status
markers printed by Damien.

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
| 6 | Negative | `deadline` | ` OOPS!!! A deadline needs a /by field, for example: deadline return book /by 2019-10-15.` |
| 7 | Positive | `deadline return book /by 2/12/2019 1800` | `[D][ ] return book (by: Dec 2 2019, 6:00 PM)`; `Now you have 2 tasks in the list.` |
| 8 | Negative | `deadline return book` | ` OOPS!!! A deadline needs a /by field, for example: deadline return book /by 2019-10-15.` |
| 9 | Negative | `deadline /by 2019-12-02` | ` OOPS!!! A deadline needs a description before /by, for example: deadline return book /by 2019-10-15.` |
| 10 | Negative | `deadline return book /by` | ` OOPS!!! A deadline needs a date or time after /by, for example: deadline return book /by 2019-10-15.` |
| 11 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 12 | Negative | `event` | ` OOPS!!! An event needs a /from field, for example: event meeting /from 2pm /to 4pm.` |
| 13 | Positive | `event project meeting /from Mon 2pm /to 4pm` | `[E][ ] project meeting (from: Mon 2pm to: 4pm)`; `Now you have 3 tasks in the list.` |
| 14 | Negative | `event project meeting /to 4pm` | ` OOPS!!! An event needs a /from field, for example: event meeting /from 2pm /to 4pm.` |
| 15 | Negative | `event project meeting /from Mon 2pm` | ` OOPS!!! An event needs a /to field, for example: event meeting /from 2pm /to 4pm.` |
| 16 | Negative | `event /from Mon 2pm /to 4pm` | ` OOPS!!! An event needs a description before /from, for example: event meeting /from 2pm /to 4pm.` |
| 17 | Negative | `event project meeting /from /to 4pm` | ` OOPS!!! An event needs a start time after /from, for example: event meeting /from 2pm /to 4pm.` |
| 18 | Negative | `event project meeting /from Mon 2pm /to` | ` OOPS!!! An event needs an end time after /to, for example: event meeting /from 2pm /to 4pm.` |
| 19 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 20 | Positive | `find book` | `Here are the matching tasks in your list:`; `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 21 | Positive | `find BOOK` | `Here are the matching tasks in your list:`; `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 22 | Negative | `find` | ` OOPS!!! Please provide a keyword after find, for example: find book.` |
| 23 | Negative | `mark` | ` OOPS!!! Please provide a task number after mark, for example: mark 1.` |
| 24 | Negative | `mark abc` | ` OOPS!!! The task number after mark must be a positive integer, for example: mark 1.` |
| 25 | Negative | `mark 0` | ` OOPS!!! Task numbers start at 1. Use list to see valid task numbers.` |
| 26 | Negative | `mark 4` | ` OOPS!!! Task 4 does not exist. Use list to see valid task numbers.` |
| 27 | Positive | `mark 1` | `[T][X] borrow book`; `Nice! I've marked this task as done:` |
| 28 | Positive state check | `list` | `1.[T][X] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 29 | Negative | `unmark` | ` OOPS!!! Please provide a task number after unmark, for example: unmark 1.` |
| 30 | Negative | `unmark abc` | ` OOPS!!! The task number after unmark must be a positive integer, for example: unmark 1.` |
| 31 | Negative | `unmark 0` | ` OOPS!!! Task numbers start at 1. Use list to see valid task numbers.` |
| 32 | Negative | `unmark 4` | ` OOPS!!! Task 4 does not exist. Use list to see valid task numbers.` |
| 33 | Positive | `unmark 1` | `[T][ ] borrow book`; `OK, I've marked this task as not done yet:` |
| 34 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)`; `3.[E][ ] project meeting (from: Mon 2pm to: 4pm)` |
| 35 | Negative | `delete` | ` OOPS!!! Please provide a task number after delete, for example: delete 1.` |
| 36 | Negative | `delete abc` | ` OOPS!!! The task number after delete must be a positive integer, for example: delete 1.` |
| 37 | Negative | `delete 0` | ` OOPS!!! Task numbers start at 1. Use list to see valid task numbers.` |
| 38 | Negative | `delete 4` | ` OOPS!!! Task 4 does not exist. Use list to see valid task numbers.` |
| 39 | Positive | `delete 3` | `[E][ ] project meeting (from: Mon 2pm to: 4pm)`; `Now you have 2 tasks in the list.` |
| 40 | Positive state check | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 41 | Positive termination | `bye` | `Bye. Hope to see you again soon!` |

## Pass criteria

The Gradle test run passes when compilation succeeds and every JUnit test
passes. The manual CLI run passes when the startup greeting is present, every
response contains its expected output fragments, Damien exits with status 0,
and all 41 main-session inputs complete. Run the persistence and corrupted-data
cases in fresh CLI sessions when checking storage behavior. Any compiler,
runtime, or missing-output failure means that the test run did not pass.

## Persistence test cases in execution order

These cases run in four fresh Damien processes after the main session. All
processes use the same temporary runtime directory, so each process must load
the tasks saved by the previous one. Together they cover saving and loading
ToDos, deadlines, and events, as well as add, mark, delete, and unmark changes.

| Session | Order | Input | Expected output fragments |
| --- | --- | --- | --- |
| 1 | 1 | `event planning /from Mon 2pm /to 4pm` | `[E][ ] planning (from: Mon 2pm to: 4pm)`; `Now you have 3 tasks in the list.` |
| 1 | 2 | `mark 1` | `[T][X] borrow book`; `Nice! I've marked this task as done:` |
| 1 | 3 | `bye` | `Bye. Hope to see you again soon!` |
| 2 | 1 | `list` | `1.[T][X] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)`; `3.[E][ ] planning (from: Mon 2pm to: 4pm)` |
| 2 | 2 | `delete 3` | `[E][ ] planning (from: Mon 2pm to: 4pm)`; `Now you have 2 tasks in the list.` |
| 2 | 3 | `bye` | `Bye. Hope to see you again soon!` |
| 3 | 1 | `list` | `1.[T][X] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 3 | 2 | `unmark 1` | `[T][ ] borrow book`; `OK, I've marked this task as not done yet:` |
| 3 | 3 | `bye` | `Bye. Hope to see you again soon!` |
| 4 | 1 | `list` | `1.[T][ ] borrow book`; `2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)` |
| 4 | 2 | `bye` | `Bye. Hope to see you again soon!` |

## Corrupted data test cases

Create a separate runtime with two valid records and two malformed records
before starting the CLI. Damien must warn about the malformed records, keep the
valid records, and continue accepting commands.
The startup output must contain `Warning: I found 2 invalid saved task records and skipped them.`

| Order | Input | Expected output fragments |
| --- | --- | --- |
| 1 | `list` | `1.[T][ ] keep this task`; `2.[D][ ] return book (by: Dec 2 2019)` |
| 2 | `bye` | `Bye. Hope to see you again soon!` |
