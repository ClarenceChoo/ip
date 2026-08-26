import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks saving and loading tasks using real temporary files.
 */
public class StorageTest {
    /**
     * Runs storage tests without leaving files in the repository.
     *
     * @param args command-line arguments; not used
     * @throws IOException if temporary test files cannot be managed
     */
    public static void main(String[] args) throws IOException {
        loadsAnEmptyListWhenTheDataFileIsMissing();
        createsDirectoriesAndRoundTripsEveryTaskType();
        preservesDateOnlyAndExplicitMidnight();
        preservesSeparatorsAndBackslashesInTaskText();
        rejectsCorruptedData();
    }

    private static void preservesDateOnlyAndExplicitMidnight() throws IOException {
        Path dataFile = Files.createTempFile("choo-storage-midnight-", ".txt");
        Storage storage = new Storage(dataFile);

        try {
            storage.save(List.of(
                    new Deadline("date only", "2019-12-02"),
                    new Deadline("midnight", "2019-12-02 0000")));
            List<Task> loadedTasks = storage.load();

            String lineSeparator = System.lineSeparator();
            assertEquals("D | 0 | date only | 2019-12-02" + lineSeparator
                    + "D | 0 | midnight | 2019-12-02 0000" + lineSeparator,
                    Files.readString(dataFile));
            assertEquals("[D][ ] date only (by: Dec 2 2019)", loadedTasks.get(0).toString());
            assertEquals("[D][ ] midnight (by: Dec 2 2019, 12:00AM)",
                    loadedTasks.get(1).toString());
        } catch (ChooException exception) {
            throw new AssertionError("Date-only and midnight deadlines should round-trip", exception);
        }
    }

    private static void loadsAnEmptyListWhenTheDataFileIsMissing() throws IOException {
        Path testDirectory = Files.createTempDirectory("choo-storage-missing-");
        Storage storage = new Storage(testDirectory.resolve("data").resolve("choo.txt"));

        try {
            assertEquals(0, storage.load().size());
        } catch (ChooException exception) {
            throw new AssertionError("A missing file should load an empty task list", exception);
        }
    }

    private static void createsDirectoriesAndRoundTripsEveryTaskType() throws IOException {
        Path testDirectory = Files.createTempDirectory("choo-storage-roundtrip-");
        Path dataFile = testDirectory.resolve("missing").resolve("folder").resolve("choo.txt");
        Storage storage = new Storage(dataFile);
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Task deadline = new Deadline("return book", "2019-12-06 1700");
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("project meeting", "Monday 2pm", "4pm"));

        try {
            storage.save(tasks);
            List<Task> loadedTasks = storage.load();

            String lineSeparator = System.lineSeparator();
            assertEquals("T | 0 | read book" + lineSeparator
                    + "D | 1 | return book | 2019-12-06 1700" + lineSeparator
                    + "E | 0 | project meeting | Monday 2pm | 4pm" + lineSeparator,
                    Files.readString(dataFile));
            assertEquals(3, loadedTasks.size());
            assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
            assertEquals("[D][X] return book (by: Dec 6 2019, 5:00PM)",
                    loadedTasks.get(1).toString());
            assertEquals("[E][ ] project meeting (from: Monday 2pm to: 4pm)",
                    loadedTasks.get(2).toString());
        } catch (ChooException exception) {
            throw new AssertionError("Valid tasks should round-trip through storage", exception);
        }
    }

    private static void preservesSeparatorsAndBackslashesInTaskText() throws IOException {
        Path dataFile = Files.createTempFile("choo-storage-escaping-", ".txt");
        Storage storage = new Storage(dataFile);

        try {
            storage.save(List.of(new Deadline("read A | B \\ notes", "2019-12-07")));
            List<Task> loadedTasks = storage.load();

            assertEquals("D | 0 | read A \\| B \\\\ notes | 2019-12-07"
                            + System.lineSeparator(),
                    Files.readString(dataFile));
            assertEquals("[D][ ] read A | B \\ notes (by: Dec 7 2019)",
                    loadedTasks.get(0).toString());
        } catch (ChooException exception) {
            throw new AssertionError("Escaped task text should round-trip", exception);
        }
    }

    private static void rejectsCorruptedData() throws IOException {
        Path dataFile = Files.createTempFile("choo-storage-corrupt-", ".txt");
        Storage storage = new Storage(dataFile);

        assertCorrupted(storage, dataFile, "D | 2 | report | Friday\n");
        assertLegacyDeadlineExplained(storage, dataFile);
        assertCorrupted(storage, dataFile, "T | 0 | \n");
        assertCorrupted(storage, dataFile, "T | 0 | bad\\q\n");
    }

    private static void assertLegacyDeadlineExplained(Storage storage, Path dataFile)
            throws IOException {
        String legacyData = "D | 0 | report | Friday\n";
        Files.writeString(dataFile, legacyData);
        try {
            storage.load();
            throw new AssertionError("A legacy free-form deadline should be rejected");
        } catch (ChooException exception) {
            assertContains(exception.getMessage(), "line 1");
            assertContains(exception.getMessage(), "yyyy-MM-dd");
            assertEquals(legacyData, Files.readString(dataFile));
        }
    }

    private static void assertCorrupted(Storage storage, Path dataFile, String data)
            throws IOException {
        Files.writeString(dataFile, data);
        try {
            storage.load();
            throw new AssertionError("Corrupted data should be rejected");
        } catch (ChooException exception) {
            assertContains(exception.getMessage(), "line 1");
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

    private static void assertContains(String actual, String expectedPart) {
        if (!actual.contains(expectedPart)) {
            throw new AssertionError("Expected to find: " + expectedPart
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
