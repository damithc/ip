# Damien User Guide

## Using the GUI

Damien's main interface is a JavaFX window. Start it from the project root
with:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew run
```

The workspace can be resized to suit a user's screen. Damien keeps the
conversation readable while the command field and `Send` button remain
anchored to the bottom of the window.

User commands and Damien's responses use separate high-contrast message
bubbles, with compact avatars and spacing designed for quick scanning.
The header has a dedicated content area so the startup greeting remains fully
visible below it, while the command bar keeps the input field and `Send` action
together.

Enter the same commands described below in the text box. Press `Enter` or
click `Send` to submit a command; both the command and Damien's response are
shown in the conversation area. The conversation scrolls automatically as it
grows. Entering `bye` displays Damien's goodbye message and closes the window.
The old text UI is deprecated and can be run with `./gradlew runCli`.

Damien keeps a list of three kinds of tasks: ToDos, deadlines, and events.

## Adding a ToDo

Use `todo` followed by a task description:

```
todo borrow book
```

Damien displays the new task as follows:

```
[T][ ] borrow book
```

## Adding a deadline

Use `/by` to separate the task description from the deadline. Damien stores
the deadline as a date and displays it in a friendlier format. Use
`yyyy-MM-dd` for a date, or add a 24-hour time in `HHmm` format:

```
deadline return book /by 2019-12-02
```

The task is displayed as:

```
[D][ ] return book (by: Dec 2 2019)
```

Deadlines can include a time. Damien accepts both the ISO-style date and the
original day/month/year form:

```
deadline return book /by 2019-12-02 1800
deadline submit report /by 2/12/2019 1800
```

The second task is displayed as:

```
[D][ ] submit report (by: Dec 2 2019, 6:00 PM)
```

## Adding an event

Use `/from` and `/to` to specify the start and end date or time:

```
event project meeting /from Mon 2pm /to 4pm
```

The task is displayed as:

```
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Viewing and updating tasks

Use `list` to view all tasks. Use `mark 1` or `unmark 1` to change the
completion status of the first task in the list. Use `delete 1` to remove the
first task from the list.

## Finding tasks

Use `find` followed by a keyword to view tasks whose descriptions contain that
keyword. Searches are case-insensitive:

```
find book
```

Damien displays the matching tasks in their original order:

```
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Jun 6 2019)
```
