package choo;

import java.nio.file.Path;

import choo.exception.ChooException;
import choo.parser.ParsedCommand;
import choo.parser.Parser;
import choo.storage.Storage;
import choo.task.Task;
import choo.task.TaskList;
import choo.ui.Ui;

/**
 * Entry point for the CHOO chatbot.
 */
public class Choo {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates CHOO with its persistence and user-interface collaborators.
     *
     * @param storage Storage used to load and save tasks.
     * @param ui User interface used for console interaction.
     */
    public Choo(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        Storage storage = new Storage(Path.of("data", "choo.txt"));
        new Choo(storage, new Ui()).run();
    }

    /**
     * Runs the chatbot using the supplied storage location.
     *
     * @param storage Storage used to load and save tasks.
     */
    public static void run(Storage storage) {
        new Choo(storage, new Ui()).run();
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

    /**
     * Executes a parsed command and reports whether the session should end.
     *
     * @param parsedCommand Command and associated details to execute.
     * @return {@code true} when the command ends the session.
     * @throws ChooException If the command cannot be completed.
     */
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
        case ADD:
            addTask(parsedCommand.getTask());
            return false;
        default:
            throw new AssertionError("Unhandled command type: " + parsedCommand.getType());
        }
    }

    /**
     * Updates and saves a task's completion status, restoring it if saving fails.
     *
     * @param taskNumber One-based position of the task to update.
     * @param isMarking Whether the task should be marked as completed.
     * @throws ChooException If the position is invalid or the change cannot be saved.
     */
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

    /**
     * Deletes and saves a task, reinserting it at its original position if saving fails.
     *
     * @param taskNumber One-based position of the task to delete.
     * @throws ChooException If the position is invalid or the deletion cannot be saved.
     */
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

    /**
     * Adds and saves a task, removing it again if saving fails.
     *
     * @param task Task to add.
     * @throws ChooException If the addition cannot be saved.
     */
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
