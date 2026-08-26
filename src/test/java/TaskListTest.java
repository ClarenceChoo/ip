import java.util.ArrayList;
import java.util.List;

/**
 * Checks task collection operations and one-based position validation.
 */
public class TaskListTest {
    /**
     * Runs focused task-list checks.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) throws ChooException {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] first", tasks.get(1).toString());

        tasks.add(new Todo("third"));
        tasks.add(2, new Todo("second"));
        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());

        Task removed = tasks.remove(2);
        assertEquals("[T][ ] second", removed.toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
        assertEquals(2, tasks.asList().size());

        assertInvalidPosition(tasks, 0);
        assertInvalidPosition(tasks, 3);
    }

    private static void assertInvalidPosition(TaskList tasks, int taskNumber) {
        try {
            tasks.get(taskNumber);
            throw new AssertionError("Expected invalid task number: " + taskNumber);
        } catch (ChooException exception) {
            assertEquals("Task number " + taskNumber + " is outside the list.",
                    exception.getMessage());
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
