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
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
                System.out.println(separator);
                continue;
            }

            if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                Task task = tasks.get(taskNumber - 1);
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
                System.out.println(separator);
                continue;
            }

            if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                Task task = tasks.get(taskNumber - 1);
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task);
                System.out.println(separator);
                continue;
            }

            if (command.startsWith("todo ")) {
                Task task = new Todo(command.substring(5));
                addTask(tasks, task, separator);
                continue;
            }

            if (command.startsWith("deadline ")) {
                String taskDetails = command.substring(9);
                int byIndex = taskDetails.indexOf(" /by ");
                String description = taskDetails.substring(0, byIndex);
                String by = taskDetails.substring(byIndex + 5);
                Task task = new Deadline(description, by);
                addTask(tasks, task, separator);
                continue;
            }

            if (command.startsWith("event ")) {
                String taskDetails = command.substring(6);
                int fromIndex = taskDetails.indexOf(" /from ");
                int toIndex = taskDetails.indexOf(" /to ", fromIndex + 7);
                String description = taskDetails.substring(0, fromIndex);
                String from = taskDetails.substring(fromIndex + 7, toIndex);
                String to = taskDetails.substring(toIndex + 5);
                Task task = new Event(description, from, to);
                addTask(tasks, task, separator);
                continue;
            }

            Task task = new Todo(command);
            addTask(tasks, task, separator);
        }
    }

    private static void addTask(ArrayList<Task> tasks, Task task, String separator) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(separator);
    }
}
