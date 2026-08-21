# Console UI Test Plan

Run this plan with the project-specific `test-ui` skill. Expected output is
compared exactly, including spaces, separators, and line order.

## Test case: Level 4 task types

Aim: Verify ToDo, Deadline, and Event parsing, arbitrary date strings, task counts, marking, listing, and exit.

Input:
```text
todo borrow book
deadline return book /by Sunday
deadline do homework /by no idea :-p
event project meeting /from Mon 2pm /to 4pm
mark 2
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
Hello! I'm CHOO.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 3 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Sunday)
3.[D][ ] do homework (by: no idea :-p)
4.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
