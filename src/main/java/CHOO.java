import java.nio.file.Path;
import java.util.List;
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
        run(new Storage(Path.of("data", "choo.txt")));
    }

    /**
     * Runs the chatbot using the supplied storage location.
     *
     * @param storage storage used to load and save tasks
     */
    public static void run(Storage storage) {
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

        List<Task> tasks;
        try {
            tasks = storage.load();
        } catch (ChooException exception) {
            System.out.println("OOPS!!! " + exception.getMessage());
            System.out.println(separator);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            try {
                if (executeCommand(command, tasks, storage, separator)) {
                    break;
                }
            } catch (ChooException exception) {
                System.out.println("OOPS!!! " + exception.getMessage());
                System.out.println(separator);
            }
        }
    }

    private static boolean executeCommand(String command, List<Task> tasks,
                                          Storage storage, String separator)
            throws ChooException {
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
            updateTaskStatus(trimmedCommand, "mark", tasks, storage, separator, true);
            return false;
        }

        if (trimmedCommand.equals("unmark") || trimmedCommand.startsWith("unmark ")) {
            updateTaskStatus(trimmedCommand, "unmark", tasks, storage, separator, false);
            return false;
        }

        if (trimmedCommand.equals("delete") || trimmedCommand.startsWith("delete ")) {
            deleteTask(trimmedCommand, tasks, storage, separator);
            return false;
        }

        if (trimmedCommand.equals("todo") || trimmedCommand.startsWith("todo ")) {
            addTodo(trimmedCommand, tasks, storage, separator);
            return false;
        }

        if (trimmedCommand.equals("deadline") || trimmedCommand.startsWith("deadline ")) {
            addDeadline(trimmedCommand, tasks, storage, separator);
            return false;
        }

        if (trimmedCommand.equals("event") || trimmedCommand.startsWith("event ")) {
            addEvent(trimmedCommand, tasks, storage, separator);
            return false;
        }

        throw new ChooException("I don't recognize that command.");
    }

    private static void updateTaskStatus(String command, String keyword,
                                         List<Task> tasks, Storage storage,
                                         String separator,
                                         boolean isMarking) throws ChooException {
        int taskNumber = getTaskNumber(command, keyword, tasks);
        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.isDone();
        if (isMarking) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        if (isMarking) {
            System.out.println("Nice! I've marked this task as done:");
        } else {
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
        System.out.println(separator);
    }

    private static void deleteTask(String command, List<Task> tasks, Storage storage,
                                   String separator) throws ChooException {
        int taskNumber = getTaskNumber(command, "delete", tasks);
        Task removedTask = tasks.remove(taskNumber - 1);
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(separator);
    }

    private static int getTaskNumber(String command, String keyword,
                                     List<Task> tasks) throws ChooException {
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
        return taskNumber;
    }

    private static void addTodo(String command, List<Task> tasks, Storage storage,
                                String separator) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        addTask(tasks, new Todo(description), storage, separator);
    }

    private static void addDeadline(String command, List<Task> tasks, Storage storage,
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
        addTask(tasks, new Deadline(description, by), storage, separator);
    }

    private static void addEvent(String command, List<Task> tasks, Storage storage,
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
        addTask(tasks, new Event(description, from, to), storage, separator);
    }

    private static void addTask(List<Task> tasks, Task task, Storage storage,
                                String separator) throws ChooException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(separator);
    }
}
