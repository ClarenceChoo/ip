package choo.ui;

import choo.task.Todo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        ui.showError("bad command");
        ui.showBye();

        String separator = "____________________________________________________________\n";
        String expected = separator
                + "##### #   # ##### #####\n"
                + "#     #   # #   # #   #\n"
                + "#     ##### #   # #   #\n"
                + "#     #   # #   # #   #\n"
                + "##### #   # ##### #####\n"
                + "Hello! I'm CHOO.\n"
                + "What can I do for you?\n"
                + separator
                + "Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + separator
                + "OOPS!!! bad command\n"
                + separator
                + "Bye. Hope to see you again soon!\n"
                + separator;
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

}
