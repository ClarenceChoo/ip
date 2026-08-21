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

            tasks.add(new Task(command));
            System.out.println("added: " + command);
            System.out.println(separator);
        }
    }
}
