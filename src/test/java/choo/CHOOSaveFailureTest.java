package choo;

import choo.exception.ChooException;
import choo.storage.Storage;
import choo.task.Task;
import choo.task.Todo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks that a failed disk write does not change CHOO's in-memory task list.
 */
public class CHOOSaveFailureTest {
    /**
     * Runs CHOO against a real path whose parent is a file, forcing save failure.
     *
     * @param args command-line arguments; not used
     * @throws Exception if temporary test setup fails
     */
    public static void main(String[] args) throws Exception {
        rejectsAnAdditionWhenSavingFails();
        restoresStatusAndDeletionWhenSavingFails();
    }

    private static void rejectsAnAdditionWhenSavingFails() throws Exception {
        Path blockingFile = Files.createTempFile("choo-blocked-parent-", ".tmp");
        Storage storage = new Storage(blockingFile.resolve("choo.txt"));
        String actualOutput = runChoo(storage, "todo should not remain\nlist\nbye\n");

        assertContains(actualOutput, "OOPS!!! I couldn't save the task data file.");
        assertContains(actualOutput, "Here are the tasks in your list:\n"
                + "____________________________________________________________");
        assertDoesNotContain(actualOutput, "1.[T][ ] should not remain");
    }

    private static void restoresStatusAndDeletionWhenSavingFails() throws Exception {
        Todo incompleteTask = new Todo("incomplete");
        Todo completedTask = new Todo("completed");
        completedTask.markAsDone();
        Storage storage = new AlwaysFailingStorage(List.of(incompleteTask, completedTask));
        String input = "mark 1\nunmark 2\ndelete 1\nlist\nbye\n";
        String actualOutput = runChoo(storage, input);

        assertContains(actualOutput, "1.[T][ ] incomplete");
        assertContains(actualOutput, "2.[T][X] completed");
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

    private static void assertContains(String actual, String expectedPart) {
        if (!actual.contains(expectedPart)) {
            throw new AssertionError("Expected to find: " + expectedPart
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }

    private static void assertDoesNotContain(String actual, String unexpectedPart) {
        if (actual.contains(unexpectedPart)) {
            throw new AssertionError("Did not expect to find: " + unexpectedPart
                    + System.lineSeparator() + "Actual: " + actual);
        }
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
