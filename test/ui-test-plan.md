# Console UI Test Plan

Run this plan with the project-specific `test-ui` skill. Expected output is
compared exactly, including spaces, separators, and line order.

## Test case: Level 3 task status workflow

Aim: Verify that tasks can be added, marked, unmarked, listed, and followed by a clean exit.

Input:
```text
read book
return book
mark 2
list
unmark 2
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
added: read book
____________________________________________________________
added: return book
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
1.[ ] read book
2.[X] return book
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
1.[ ] read book
2.[ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
