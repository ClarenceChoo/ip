package choo.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import choo.task.Task;

/**
 * Handles console input and user-visible output for CHOO.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "##### #   # ##### #####\n"
            + "#     #   # #   # #   #\n"
            + "#     ##### #   # #   #\n"
            + "#     #   # #   # #   #\n"
            + "##### #   # ##### #####\n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the process console.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param input stream from which commands are read.
     * @param output stream to which responses are written.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command can be read.
     *
     * @return true if another command is available.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command.
     *
     * @return next command line.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Displays the CHOO banner and greeting.
     */
    public void showWelcome() {
        showSeparator();
        this.output.print(BANNER);
        this.output.println("Hello! I'm CHOO.");
        this.output.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Displays all tasks with one-based task numbers.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        this.output.println("Here are the tasks in your list:");
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        this.output.println("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
        showSeparator();
    }

    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount number of tasks after adding.
     */
    public void showAddedTask(Task task, int taskCount) {
        this.output.println("Got it. I've added this task:");
        this.output.println("  " + task);
        showTaskCount(taskCount);
        showSeparator();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task.
     * @param taskCount number of tasks after deleting.
     */
    public void showDeletedTask(Task task, int taskCount) {
        this.output.println("Noted. I've removed this task:");
        this.output.println("  " + task);
        showTaskCount(taskCount);
        showSeparator();
    }

    /**
     * Displays confirmation that a task completion status changed.
     *
     * @param task updated task.
     * @param isMarked true if the task was marked done.
     */
    public void showTaskStatusChanged(Task task, boolean isMarked) {
        if (isMarked) {
            this.output.println("Nice! I've marked this task as done:");
        } else {
            this.output.println("OK, I've marked this task as not done yet:");
        }
        this.output.println("  " + task);
        showSeparator();
    }

    /**
     * Displays a user-facing error.
     *
     * @param message error details.
     */
    public void showError(String message) {
        this.output.println("OOPS!!! " + message);
        showSeparator();
    }

    /**
     * Displays the farewell message.
     */
    public void showBye() {
        this.output.println("Bye. Hope to see you again soon!");
        showSeparator();
    }

    private void showTaskCount(int taskCount) {
        this.output.println("Now you have " + taskCount + " tasks in the list.");
    }

    private void showSeparator() {
        this.output.println(SEPARATOR);
    }
}
