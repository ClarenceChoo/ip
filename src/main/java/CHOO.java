import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;

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
        Ui ui = new Ui();
        ui.showWelcome();

        List<Task> tasks;
        try {
            tasks = storage.load();
        } catch (ChooException exception) {
            ui.showError(exception.getMessage());
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                if (executeCommand(command, tasks, storage, ui)) {
                    break;
                }
            } catch (ChooException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    private static boolean executeCommand(String command, List<Task> tasks,
                                          Storage storage, Ui ui)
            throws ChooException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals("bye")) {
            ui.showBye();
            return true;
        }

        if (trimmedCommand.equals("list")) {
            ui.showTaskList(tasks);
            return false;
        }

        if (trimmedCommand.equals("mark") || trimmedCommand.startsWith("mark ")) {
            updateTaskStatus(trimmedCommand, "mark", tasks, storage, ui, true);
            return false;
        }

        if (trimmedCommand.equals("unmark") || trimmedCommand.startsWith("unmark ")) {
            updateTaskStatus(trimmedCommand, "unmark", tasks, storage, ui, false);
            return false;
        }

        if (trimmedCommand.equals("delete") || trimmedCommand.startsWith("delete ")) {
            deleteTask(trimmedCommand, tasks, storage, ui);
            return false;
        }

        if (trimmedCommand.equals("todo") || trimmedCommand.startsWith("todo ")) {
            addTodo(trimmedCommand, tasks, storage, ui);
            return false;
        }

        if (trimmedCommand.equals("deadline") || trimmedCommand.startsWith("deadline ")) {
            addDeadline(trimmedCommand, tasks, storage, ui);
            return false;
        }

        if (trimmedCommand.equals("event") || trimmedCommand.startsWith("event ")) {
            addEvent(trimmedCommand, tasks, storage, ui);
            return false;
        }

        throw new ChooException("I don't recognize that command.");
    }

    private static void updateTaskStatus(String command, String keyword,
                                         List<Task> tasks, Storage storage,
                                         Ui ui,
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
        ui.showTaskStatusChanged(task, isMarking);
    }

    private static void deleteTask(String command, List<Task> tasks, Storage storage,
                                   Ui ui) throws ChooException {
        int taskNumber = getTaskNumber(command, "delete", tasks);
        Task removedTask = tasks.remove(taskNumber - 1);
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        ui.showDeletedTask(removedTask, tasks.size());
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
                                Ui ui) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        addTask(tasks, new Todo(description), storage, ui);
    }

    private static void addDeadline(String command, List<Task> tasks, Storage storage,
                                    Ui ui) throws ChooException {
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
        try {
            addTask(tasks, new Deadline(description, by), storage, ui);
        } catch (DateTimeParseException exception) {
            throw new ChooException(
                    "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        }
    }

    private static void addEvent(String command, List<Task> tasks, Storage storage,
                                 Ui ui) throws ChooException {
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
        addTask(tasks, new Event(description, from, to), storage, ui);
    }

    private static void addTask(List<Task> tasks, Task task, Storage storage,
                                Ui ui) throws ChooException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
