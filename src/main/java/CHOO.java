import java.nio.file.Path;
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
        ParsedCommand parsedCommand = Parser.parse(command);
        switch (parsedCommand.getType()) {
        case BYE:
            ui.showBye();
            return true;
        case LIST:
            ui.showTaskList(tasks);
            return false;
        case MARK:
            updateTaskStatus(parsedCommand.getTaskNumber(), tasks, storage, ui, true);
            return false;
        case UNMARK:
            updateTaskStatus(parsedCommand.getTaskNumber(), tasks, storage, ui, false);
            return false;
        case DELETE:
            deleteTask(parsedCommand.getTaskNumber(), tasks, storage, ui);
            return false;
        case ADD:
            addTask(tasks, parsedCommand.getTask(), storage, ui);
            return false;
        default:
            throw new AssertionError("Unhandled command type: " + parsedCommand.getType());
        }
    }

    private static void updateTaskStatus(int taskNumber, List<Task> tasks, Storage storage,
                                         Ui ui,
                                         boolean isMarking) throws ChooException {
        validateTaskNumber(taskNumber, tasks);
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

    private static void deleteTask(int taskNumber, List<Task> tasks, Storage storage,
                                   Ui ui) throws ChooException {
        validateTaskNumber(taskNumber, tasks);
        Task removedTask = tasks.remove(taskNumber - 1);
        try {
            storage.save(tasks);
        } catch (ChooException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        ui.showDeletedTask(removedTask, tasks.size());
    }

    private static void validateTaskNumber(int taskNumber, List<Task> tasks)
            throws ChooException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ChooException(
                    "Task number " + taskNumber + " is outside the list.");
        }
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
