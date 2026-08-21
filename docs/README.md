# CHOO User Guide

CHOO is a command-line chatbot that keeps track of your tasks.

// Product screenshot goes here

Enter one command at a time and press Enter. Enter `bye` when you want to exit.

## Adding a task

Enter the task description to add it to the list.

Example: `read book`

## Listing tasks

Enter `list` to display all tasks. An `[X]` means that a task is done, while
`[ ]` means that it is not done.

Example output:

```text
1.[ ] read book
2.[X] return book
```

## Marking a task as done

Enter `mark NUMBER`, where `NUMBER` is the task number shown by `list`.

Example: `mark 2`

## Marking a task as not done

Enter `unmark NUMBER`, where `NUMBER` is the task number shown by `list`.

Example: `unmark 2`
