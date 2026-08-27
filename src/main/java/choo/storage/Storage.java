package choo.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import choo.exception.ChooException;
import choo.task.Deadline;
import choo.task.Event;
import choo.task.Task;
import choo.task.Todo;

/**
 * Saves tasks to a text file and restores them between CHOO sessions.
 */
public class Storage {
    private static final DateTimeFormatter STORED_DEADLINE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter STORED_DEADLINE_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");

    private final Path dataFile;

    /**
     * Creates storage that uses the given data file.
     *
     * @param dataFile relative or absolute path used for task data.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads all tasks from disk, or returns an empty list on first use.
     *
     * @return tasks restored from the data file.
     * @throws ChooException if the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws ChooException {
        if (!Files.exists(this.dataFile)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(this.dataFile, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                tasks.add(parseTask(lines.get(i), i + 1));
            }
            return tasks;
        } catch (IOException exception) {
            throw new ChooException("I couldn't read the task data file.");
        }
    }

    /**
     * Writes the complete task list, creating missing parent directories.
     *
     * @param tasks tasks to save.
     * @throws ChooException if the data file cannot be written.
     */
    public void save(List<Task> tasks) throws ChooException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }

        Path temporaryFile = null;
        try {
            Path parent = this.dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            temporaryFile = this.dataFile.resolveSibling(
                    this.dataFile.getFileName() + ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } catch (IOException exception) {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            ChooException storageException = new ChooException(
                    "I couldn't save the task data file.");
            storageException.initCause(exception);
            throw storageException;
        }
    }

    /**
     * Serializes a task into the escaped, pipe-delimited storage format.
     *
     * @param task Task to serialize.
     * @return One complete storage line.
     */
    private static String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            DateTimeFormatter storageFormat = deadline.hasTime()
                    ? STORED_DEADLINE_DATE_TIME_FORMAT : STORED_DEADLINE_DATE_FORMAT;
            return "D | " + status + " | " + description
                    + " | " + deadline.getBy().format(storageFormat);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + status + " | " + description
                    + " | " + escape(event.getFrom())
                    + " | " + escape(event.getTo());
        }
        return "T | " + status + " | " + description;
    }

    /**
     * Restores one task from a storage line and validates every stored field.
     *
     * @param line Storage line to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Restored task.
     * @throws ChooException If the line is corrupted or uses an unsupported date.
     */
    private static Task parseTask(String line, int lineNumber) throws ChooException {
        List<String> fields = splitEscapedFields(line, lineNumber);
        if (fields.size() < 2 || !(fields.get(1).equals("0") || fields.get(1).equals("1"))) {
            throw corruptedData(lineNumber);
        }

        Task task;
        String type = fields.get(0);
        if (type.equals("T") && fields.size() == 3) {
            task = new Todo(fields.get(2));
        } else if (type.equals("D") && fields.size() == 4) {
            try {
                task = new Deadline(fields.get(2), fields.get(3));
            } catch (DateTimeParseException exception) {
                throw unsupportedDeadlineData(lineNumber);
            }
        } else if (type.equals("E") && fields.size() == 5) {
            task = new Event(fields.get(2), fields.get(3), fields.get(4));
        } else {
            throw corruptedData(lineNumber);
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isEmpty()) {
                throw corruptedData(lineNumber);
            }
        }

        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a storage line while decoding escaped separators and backslashes.
     *
     * @param line Storage line to split.
     * @param lineNumber One-based line number used in error messages.
     * @return Decoded and trimmed fields.
     * @throws ChooException If the line contains an invalid escape sequence.
     */
    private static List<String> splitEscapedFields(String line, int lineNumber)
            throws ChooException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw corruptedData(lineNumber);
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw corruptedData(lineNumber);
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes storage-format separators and escape characters in task text.
     *
     * @param text Text to encode.
     * @return Escaped text safe for a storage field.
     */
    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Replaces the data file atomically when supported, with a portable fallback.
     *
     * @param temporaryFile Fully written temporary file.
     * @throws IOException If neither replacement strategy succeeds.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, this.dataFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, this.dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static ChooException corruptedData(int lineNumber) {
        return new ChooException("The task data file is corrupted at line " + lineNumber + ".");
    }

    private static ChooException unsupportedDeadlineData(int lineNumber) {
        return new ChooException("The deadline at line " + lineNumber
                + " uses an unsupported date. Change it to yyyy-MM-dd or yyyy-MM-dd HHmm.");
    }
}
