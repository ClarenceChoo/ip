package choo;

import choo.exception.ChooException;
import choo.storage.Storage;
import choo.task.Task;
import choo.task.Todo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that a failed disk write does not change CHOO's in-memory task list.
 */
public class CHOOSaveFailureTest {
    @Test
    void run_additionCannotBeSaved_doesNotKeepTaskInMemory() throws Exception {
        Path blockingFile = Files.createTempFile("choo-blocked-parent-", ".tmp");
        Storage storage = new Storage(blockingFile.resolve("choo.txt"));
        String actualOutput = runChoo(storage, "todo should not remain\nlist\nbye\n");

        assertTrue(actualOutput.contains("OOPS!!! I couldn't save the task data file."));
        assertTrue(actualOutput.contains("Here are the tasks in your list:\n"
                + "____________________________________________________________"));
        assertFalse(actualOutput.contains("1.[T][ ] should not remain"));
    }

    @Test
    void run_statusAndDeletionCannotBeSaved_restoresPreviousState() throws Exception {
        Todo incompleteTask = new Todo("incomplete");
        Todo completedTask = new Todo("completed");
        completedTask.markAsDone();
        Storage storage = new AlwaysFailingStorage(List.of(incompleteTask, completedTask));
        String input = "mark 1\nunmark 2\ndelete 1\nlist\nbye\n";
        String actualOutput = runChoo(storage, input);

        assertTrue(actualOutput.contains("1.[T][ ] incomplete"));
        assertTrue(actualOutput.contains("2.[T][X] completed"));
    }

    private static String runChoo(Storage storage, String input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            CHOO.run(storage);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private static class AlwaysFailingStorage extends Storage {
        private final List<Task> initialTasks;

        AlwaysFailingStorage(List<Task> initialTasks) {
            super(Path.of("unused-test-data.txt"));
            this.initialTasks = initialTasks;
        }

        @Override
        public List<Task> load() {
            return new ArrayList<>(this.initialTasks);
        }

        @Override
        public void save(List<Task> tasks) throws ChooException {
            throw new ChooException("I couldn't save the task data file.");
        }
    }
}
