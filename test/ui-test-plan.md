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

## Test case: Delete tasks and reject invalid positions

Aim: Verify deletion removes the selected task, renumbers the list, and leaves state unchanged after invalid positions.

Input:
```text
todo first
deadline second /by Friday
event third /from Monday /to Tuesday
delete
delete two
delete 0
delete -1
delete 4
delete 2
list
delete 1
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
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] second (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] third (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
____________________________________________________________
OOPS!!! Enter a whole-number task position after delete.
____________________________________________________________
OOPS!!! Enter a whole-number task position after delete.
____________________________________________________________
OOPS!!! Task number 0 is outside the list.
____________________________________________________________
OOPS!!! Task number -1 is outside the list.
____________________________________________________________
OOPS!!! Task number 4 is outside the list.
____________________________________________________________
Noted. I've removed this task:
  [D][ ] second (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first
2.[E][ ] third (from: Monday to: Tuesday)
____________________________________________________________
Noted. I've removed this task:
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] third (from: Monday to: Tuesday)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Invalid task details preserve valid state

Aim: Verify descriptions and schedule delimiters are validated while successful tasks remain unchanged.

Input:
```text
todo
mystery command
todo keep this
deadline report
deadline /by Friday
deadline report /by
deadline report /by Friday
event /from Monday /to Tuesday
event meeting /from Monday
event meeting /to Tuesday
event meeting /from Monday /to
event meeting /from Monday /to Tuesday
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
OOPS!!! A todo needs a description.
____________________________________________________________
OOPS!!! I don't recognize that command.
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep this
Now you have 1 tasks in the list.
____________________________________________________________
OOPS!!! A deadline needs a /by date or time.
____________________________________________________________
OOPS!!! A deadline needs a description.
____________________________________________________________
OOPS!!! A deadline needs a /by date or time.
____________________________________________________________
Got it. I've added this task:
  [D][ ] report (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
OOPS!!! An event needs a description.
____________________________________________________________
OOPS!!! An event needs both /from and /to values.
____________________________________________________________
OOPS!!! An event needs both /from and /to values.
____________________________________________________________
OOPS!!! An event needs both /from and /to values.
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep this
2.[D][ ] report (by: Friday)
3.[E][ ] meeting (from: Monday to: Tuesday)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Invalid task numbers preserve completion state

Aim: Verify missing, non-numeric, and out-of-range positions are rejected without changing task status.

Input:
```text
todo first
mark
mark two
mark 2
mark 1
unmark
unmark 0
unmark 1
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
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
OOPS!!! Enter a whole-number task position after mark.
____________________________________________________________
OOPS!!! Enter a whole-number task position after mark.
____________________________________________________________
OOPS!!! Task number 2 is outside the list.
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] first
____________________________________________________________
OOPS!!! Enter a whole-number task position after unmark.
____________________________________________________________
OOPS!!! Task number 0 is outside the list.
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] first
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
