# Damien User Guide

Damien is a desktop task assistant for managing ToDos, deadlines, and events.
This guide is for users running the released JAR file.

## Requirements

Damien requires Java 25 or later. Check whether Java is installed by running:

```bash
java --version
```

You do not need Gradle, IntelliJ IDEA, or the project source code. The release
JAR includes Damien's JavaFX dependencies.

## Downloading and starting Damien

1. Download `damien.jar` from the [latest GitHub release](https://github.com/damithc/ip/releases/latest).
1. Create or choose a folder for Damien's saved task data, then place the JAR
   file in that folder.
1. Open a terminal in that folder and start Damien:

   ```bash
   java -jar damien.jar
   ```

Damien opens a window with a message area, a command field, and a `Send`
button. Enter a command in the field and press `Enter` or click `Send`.

### Saved tasks

Damien saves tasks in `data/duke.txt` below the folder where you run the
command. The `data` folder is created automatically when you add your first
task. Keep this file if you want to preserve your tasks, and back it up if the
task list is important.

To keep tasks in the same list, always start Damien from the same folder. Type
`bye` to close the application.

## Commands

Commands must be entered in lowercase. Task numbers refer to the numbered list
shown by `list`.

| Command | Example | Description |
| --- | --- | --- |
| `todo <description>` | `todo borrow book` | Adds a ToDo. |
| `deadline <description> /by <date>` | `deadline return book /by 2019-12-02` | Adds a deadline. |
| `event <description> /from <start> /to <end>` | `event project meeting /from Mon 2pm /to 4pm` | Adds an event. |
| `list` | `list` | Shows all tasks in their current order. |
| `mark <number>` | `mark 1` | Marks a task as completed. |
| `unmark <number>` | `unmark 1` | Marks a task as not completed. |
| `delete <number>` | `delete 1` | Removes a task. |
| `find <keyword>` | `find book` | Shows tasks whose descriptions contain the keyword. |
| `bye` | `bye` | Closes Damien. |

### Deadlines

Use `yyyy-MM-dd` for a date. To include a time, add it in 24-hour `HHmm`
format:

```text
deadline return book /by 2019-12-02
deadline submit report /by 2019-12-02 1800
```

Damien also accepts the date format `d/M/yyyy` when a time is included:

```text
deadline submit report /by 2/12/2019 1800
```

### Task status

Damien displays tasks with a type marker and a completion marker:

```text
[T][ ] borrow book
[D][X] return book (by: Dec 2 2019)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

`[T]`, `[D]`, and `[E]` identify ToDos, deadlines, and events. `[ ]` means
not completed, while `[X]` means completed.
