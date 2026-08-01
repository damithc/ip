# Damien User Guide

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

Use `/by` to separate the task description from the date or time. Dates and
times are kept as text, so you can use formats such as `Sunday` or `11/10/2019
5pm`:

```
deadline return book /by Sunday
```

The task is displayed as:

```
[D][ ] return book (by: Sunday)
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
completion status of the first task in the list.
