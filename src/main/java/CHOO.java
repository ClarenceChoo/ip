import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the CHOO chatbot.
 */
public class CHOO {
    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = "##### #   # ##### #####\n"
                + "#     #   # #   # #   #\n"
                + "#     ##### #   # #   #\n"
                + "#     #   # #   # #   #\n"
                + "##### #   # ##### #####\n";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm CHOO.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            try {
                if (executeCommand(command, tasks, separator)) {
                    break;
                }
            } catch (ChooException exception) {
                System.out.println("OOPS!!! " + exception.getMessage());
                System.out.println(separator);
            }
        }
    }

    private static boolean executeCommand(String command, ArrayList<Task> tasks,
                                          String separator) throws ChooException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals("bye")) {
            System.out.println("Bye. Hope to see you again soon!");
            System.out.println(separator);
            return true;
        }

        if (trimmedCommand.equals("list")) {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
            System.out.println(separator);
            return false;
        }

        if (trimmedCommand.equals("mark") || trimmedCommand.startsWith("mark ")) {
            updateTaskStatus(trimmedCommand, "mark", tasks, separator, true);
            return false;
        }

        if (trimmedCommand.equals("unmark") || trimmedCommand.startsWith("unmark ")) {
            updateTaskStatus(trimmedCommand, "unmark", tasks, separator, false);
            return false;
        }

        if (trimmedCommand.equals("todo") || trimmedCommand.startsWith("todo ")) {
            addTodo(trimmedCommand, tasks, separator);
            return false;
        }

        if (trimmedCommand.equals("deadline") || trimmedCommand.startsWith("deadline ")) {
            addDeadline(trimmedCommand, tasks, separator);
            return false;
        }

        if (trimmedCommand.equals("event") || trimmedCommand.startsWith("event ")) {
            addEvent(trimmedCommand, tasks, separator);
            return false;
        }

        throw new ChooException("I don't recognize that command.");
    }

    private static void updateTaskStatus(String command, String keyword,
                                         ArrayList<Task> tasks, String separator,
                                         boolean isMarking) throws ChooException {
        String taskNumberText = command.substring(keyword.length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new ChooException(
                    "Enter a whole-number task position after " + keyword + ".");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ChooException(
                    "Task number " + taskNumber + " is outside the list.");
        }

        Task task = tasks.get(taskNumber - 1);
        if (isMarking) {
            task.markAsDone();
            System.out.println("Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
        System.out.println(separator);
    }

    private static void addTodo(String command, ArrayList<Task> tasks,
                                String separator) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        addTask(tasks, new Todo(description), separator);
    }

    private static void addDeadline(String command, ArrayList<Task> tasks,
                                    String separator) throws ChooException {
        String taskDetails = command.substring("deadline".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }

        int byIndex = taskDetails.indexOf("/by");
        if (byIndex < 0) {
            throw new ChooException("A deadline needs a /by date or time.");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }
        if (by.isEmpty()) {
            throw new ChooException("A deadline needs a /by date or time.");
        }
        addTask(tasks, new Deadline(description, by), separator);
    }

    private static void addEvent(String command, ArrayList<Task> tasks,
                                 String separator) throws ChooException {
        String taskDetails = command.substring("event".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }

        int fromIndex = taskDetails.indexOf("/from");
        int toIndex = taskDetails.indexOf("/to");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new ChooException("An event needs both /from and /to values.");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
        String to = taskDetails.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new ChooException("An event needs both /from and /to values.");
        }
        addTask(tasks, new Event(description, from, to), separator);
    }

    private static void addTask(ArrayList<Task> tasks, Task task, String separator) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(separator);
    }
}
