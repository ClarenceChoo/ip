# CHOO User Guide

CHOO is a command-line chatbot that keeps track of your tasks.

Enter one command at a time and press Enter. Enter `bye` when you want to exit.

## Understanding the task list

Each task starts with its type and completion status:

- `[T]` identifies a todo.
- `[D]` identifies a deadline.
- `[E]` identifies an event.
- `[X]` means the task is done, while `[ ]` means it is not done.

## Adding a todo

Use `todo DESCRIPTION` for a task without a date or time.

Example: `todo read book`

## Adding a deadline

Use `deadline DESCRIPTION /by DATE_OR_TIME` for a task that must be completed
by a particular date or time.

Example: `deadline submit report /by Friday 5pm`

The date or time is stored exactly as entered.

## Adding an event

Use `event DESCRIPTION /from START /to END` for a task with a start and end.

Example: `event project meeting /from Monday 2pm /to 4pm`

The start and end values are stored exactly as entered.

## Listing tasks

Use `list` to display all tasks and their task numbers.

Example output:

```text
1.[T][ ] read book
2.[D][X] return book (by: Friday)
3.[E][ ] project meeting (from: Monday 2pm to: 4pm)
```

## Marking a task as done

Use `mark NUMBER`, where `NUMBER` is the task number shown by `list`.

Example: `mark 2`

## Marking a task as not done

Use `unmark NUMBER`, where `NUMBER` is the task number shown by `list`.

Example: `unmark 2`

## Deleting a task

Use `delete NUMBER`, where `NUMBER` is the task number shown by `list`.

Example: `delete 2`

The remaining tasks are renumbered automatically.

## Handling invalid commands

CHOO explains invalid commands and missing task details without changing your
existing task list. Correct the command using the formats above and try again.

## Exiting CHOO

Use `bye` to end the session.
