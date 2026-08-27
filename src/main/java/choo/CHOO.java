package choo;

import choo.exception.ChooException;
import choo.parser.ParsedCommand;
import choo.parser.Parser;
import choo.storage.Storage;
import choo.task.Task;
import choo.task.TaskList;
import choo.ui.Ui;

import java.nio.file.Path;

/**
 * Entry point for the CHOO chatbot.
 */
public class CHOO {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates CHOO with its persistence and user-interface collaborators.
     *
     * @param storage storage used to load and save tasks
     * @param ui user interface used for console interaction
     */
    public CHOO(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Storage storage = new Storage(Path.of("data", "choo.txt"));
        new CHOO(storage, new Ui()).run();
    }

    /**
     * Runs the chatbot using the supplied storage location.
     *
     * @param storage storage used to load and save tasks
     */
    public static void run(Storage storage) {
        new CHOO(storage, new Ui()).run();
    }

    /**
     * Processes commands until input ends or the user exits.
     */
    public void run() {
        this.ui.showWelcome();

        try {
            this.tasks = new TaskList(this.storage.load());
        } catch (ChooException exception) {
            this.ui.showError(exception.getMessage());
            return;
        }

        while (this.ui.hasNextCommand()) {
            String command = this.ui.readCommand();
            try {
                if (executeCommand(Parser.parse(command))) {
                    break;
                }
            } catch (ChooException exception) {
                this.ui.showError(exception.getMessage());
            }
        }
    }

    private boolean executeCommand(ParsedCommand parsedCommand) throws ChooException {
        switch (parsedCommand.getType()) {
        case BYE:
            this.ui.showBye();
            return true;
        case LIST:
            this.ui.showTaskList(this.tasks.asList());
            return false;
        case MARK:
            updateTaskStatus(parsedCommand.getTaskNumber(), true);
            return false;
        case UNMARK:
            updateTaskStatus(parsedCommand.getTaskNumber(), false);
            return false;
        case DELETE:
            deleteTask(parsedCommand.getTaskNumber());
            return false;
        case FIND:
            this.ui.showMatchingTasks(this.tasks.find(parsedCommand.getKeyword()));
            return false;
        case ADD:
            addTask(parsedCommand.getTask());
            return false;
        default:
            throw new AssertionError("Unhandled command type: " + parsedCommand.getType());
        }
    }

    private void updateTaskStatus(int taskNumber, boolean isMarking) throws ChooException {
        Task task = this.tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        if (isMarking) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        this.ui.showTaskStatusChanged(task, isMarking);
    }

    private void deleteTask(int taskNumber) throws ChooException {
        Task removedTask = this.tasks.remove(taskNumber);
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            this.tasks.add(taskNumber, removedTask);
            throw exception;
        }
        this.ui.showDeletedTask(removedTask, this.tasks.size());
    }

    private void addTask(Task task) throws ChooException {
        this.tasks.add(task);
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            this.tasks.remove(this.tasks.size());
            throw exception;
        }
        this.ui.showAddedTask(task, this.tasks.size());
    }
}
