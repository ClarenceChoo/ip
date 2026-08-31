package choo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import choo.storage.Storage;
import choo.ui.Ui;

/**
 * Checks the user-visible behavior of the CHOO command-line interface.
 */
public class ChooTest {
    @Test
    void getResponse_sequentialGuiCommands_preservesStateAndFormatsReplies() throws Exception {
        Path dataFile = Files.createTempDirectory("choo-gui-response-test-")
                .resolve("data").resolve("choo.txt");
        Choo choo = new Choo(new Storage(dataFile), new Ui());
        String lineSeparator = System.lineSeparator();

        assertEquals("Got it. I've added this task:" + lineSeparator
                + "  [T][ ] read book" + lineSeparator
                + "Now you have 1 tasks in the list.", choo.getResponse("todo read book"));
        assertEquals("Nice! I've marked this task as done:" + lineSeparator
                + "  [T][X] read book", choo.getResponse("mark 1"));
        assertEquals("Here are the tasks in your list:" + lineSeparator
                + "1.[T][X] read book", choo.getResponse("list"));
        assertEquals("OOPS!!! Task number 2 is outside the list.", choo.getResponse("delete 2"));
        assertEquals("Bye. Hope to see you again soon!", choo.getResponse("bye"));
    }

    @Test
    void run_mixedValidAndInvalidCommands_preservesCorrectTaskState() throws Exception {
        String input = "todo\n"
                + "mystery command\n"
                + "todo keep this\n"
                + "deadline remove this /by 2019-12-06\n"
                + "event keep event /from Monday /to Tuesday\n"
                + "delete 2\n"
                + "list\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        Path dataFile = Files.createTempDirectory("choo-command-test-")
                .resolve("data").resolve("choo.txt");

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Choo.run(new Storage(dataFile));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String actualOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(actualOutput.contains("OOPS!!! A todo needs a description."));
        assertTrue(actualOutput.contains("OOPS!!! I don't recognize that command."));
        assertTrue(actualOutput.contains("Noted. I've removed this task:"));
        assertTrue(actualOutput.contains("[D][ ] remove this (by: Dec 6 2019)"));
        assertTrue(actualOutput.contains("Now you have 2 tasks in the list."));
        assertTrue(actualOutput.contains("Here are the tasks in your list:\n"
                + "1.[T][ ] keep this\n"
                + "2.[E][ ] keep event (from: Monday to: Tuesday)\n"
                + "____________________________________________________________"));
        assertFalse(actualOutput.contains("[T][ ] mystery command"));
        assertTrue(actualOutput.contains("Bye. Hope to see you again soon!"));
    }
}
