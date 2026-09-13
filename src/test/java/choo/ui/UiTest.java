package choo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import choo.task.Todo;

/**
 * Checks console input and output handled by {@link Ui}.
 */
public class UiTest {
    @Test
    void readAndShowCommands_formatsCompleteConsoleSession() {
        String input = "list\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        assertEquals("list", ui.readCommand());
        assertEquals("bye", ui.readCommand());

        ui.showWelcome();
        ui.showTaskList(List.of(new Todo("read book")));
        ui.showMatchingTasks(List.of(new Todo("return book")));
        ui.showError("bad command");
        ui.showBye();

        String lineSeparator = System.lineSeparator();
        String separator = "____________________________________________________________" + lineSeparator;
        String expected = separator
                + "##### #   # ##### #####" + lineSeparator
                + "#     #   # #   # #   #" + lineSeparator
                + "#     ##### #   # #   #" + lineSeparator
                + "#     #   # #   # #   #" + lineSeparator
                + "##### #   # ##### #####" + lineSeparator
                + "All aboard! I'm CHOO, your task conductor." + lineSeparator
                + "Tell me what needs to stay on track." + lineSeparator
                + separator
                + "Here is your task itinerary:" + lineSeparator
                + "1.[T][ ] read book" + lineSeparator
                + separator
                + "Here are the matching stops:" + lineSeparator
                + "1.[T][ ] return book" + lineSeparator
                + separator
                + "OOPS!!! Signal problem: bad command" + lineSeparator
                + separator
                + "End of the line for now. Safe travels!" + lineSeparator
                + separator;
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

}
