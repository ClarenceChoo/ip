package choo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import choo.exception.ChooException;

/**
 * Checks task collection operations and one-based position validation.
 */
public class TaskListTest {
    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() throws ChooException {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] first", tasks.get(1).toString());
    }

    @Test
    void add_validPositions_insertsAtOneBasedPosition() throws ChooException {
        TaskList tasks = new TaskList(List.of(new Todo("first")));
        tasks.add(new Todo("third"));
        tasks.add(2, new Todo("second"));

        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    void remove_validPosition_removesAndReturnsTask() throws ChooException {
        TaskList tasks = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("third")));
        Task removed = tasks.remove(2);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
        assertEquals(2, tasks.asList().size());
    }

    @Test
    void positionOperations_outsideList_throwSpecificError() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));
        assertInvalidPosition(tasks, 0);
        assertInvalidPosition(tasks, 2);
        ChooException addException = assertThrows(ChooException.class,
                () -> tasks.add(3, new Todo("invalid")));
        assertEquals("Task number 3 is outside the list.", addException.getMessage());
    }

    private static void assertInvalidPosition(TaskList tasks, int taskNumber) {
        ChooException exception = assertThrows(ChooException.class,
                () -> tasks.get(taskNumber));
        assertEquals("Task number " + taskNumber + " is outside the list.",
                exception.getMessage());
    }
}
