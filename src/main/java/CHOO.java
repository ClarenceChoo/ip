import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the CHOO chatbot.
 */
public class CHOO {
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

        ArrayList<String> tasks = new ArrayList<>();

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
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
                System.out.println(separator);
                continue;
            }

            tasks.add(command);
            System.out.println("added: " + command);
            System.out.println(separator);
        }
    }
}
