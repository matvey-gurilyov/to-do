package com.todo.repository;

import com.todo.model.Task;
import com.todo.model.TaskStatus;
import com.todo.util.DateUtil;
import com.todo.util.FileUtil;
import com.todo.util.IdGenerator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileTaskRepository implements TaskRepository {
    private static final String DELIMITER = "\\|";
    private static final String RESET_STORAGE_PROPERTY = "todo.storage.reset";
    private final Path storagePath;
    private final Map<Long, Task> tasksById = new LinkedHashMap<>();

    public FileTaskRepository(Path storagePath) {
        this.storagePath = storagePath;
        resetStorageIfRequested();
        loadFromFile();
        // Re-save loaded tasks in guaranteed UTF-8 format to normalize storage.
        persist();
    }

    @Override
    public Task save(Task task) {
        tasksById.put(task.getId(), task);
        persist();
        return task;
    }

    @Override
    public List<Task> findAll() {
        return tasksById.values().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasksById.get(id));
    }

    @Override
    public Task update(Task task) {
        tasksById.put(task.getId(), task);
        persist();
        return task;
    }

    @Override
    public boolean deleteById(long id) {
        Task removed = tasksById.remove(id);
        if (removed != null) {
            persist();
            return true;
        }
        return false;
    }

    @Override
    public long getNextId() {
        return IdGenerator.nextId(tasksById.keySet());
    }

    private void loadFromFile() {
        List<String> lines = FileUtil.readAllLines(storagePath);
        for (String line : lines) {
            String normalizedLine = removeBom(line);
            if (normalizedLine == null || normalizedLine.trim().isEmpty()) {
                continue;
            }
            String[] tokens = normalizedLine.split(DELIMITER, -1);
            if (tokens.length != 5) {
                continue;
            }
            long id = Long.parseLong(tokens[0]);
            String title = unescape(tokens[1]);
            String description = unescape(tokens[2]);
            TaskStatus status = TaskStatus.valueOf(tokens[3]);
            Task task = new Task(id, title, description, status, DateUtil.parse(tokens[4]));
            tasksById.put(id, task);
        }
    }

    private void resetStorageIfRequested() {
        if (Boolean.parseBoolean(System.getProperty(RESET_STORAGE_PROPERTY, "false"))) {
            FileUtil.writeLines(storagePath, List.of());
        }
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Task task : findAll()) {
            String line = task.getId()
                    + "|"
                    + escape(task.getTitle())
                    + "|"
                    + escape(task.getDescription())
                    + "|"
                    + task.getStatus().name()
                    + "|"
                    + DateUtil.format(task.getCreatedAt());
            lines.add(line);
        }
        FileUtil.writeLines(storagePath, lines);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' && i + 1 < value.length()) {
                char next = value.charAt(i + 1);
                if (next == 'p') {
                    result.append('|');
                    i++;
                    continue;
                }
                if (next == 'n') {
                    result.append('\n');
                    i++;
                    continue;
                }
                if (next == 'r') {
                    result.append('\r');
                    i++;
                    continue;
                }
                if (next == '\\') {
                    result.append('\\');
                    i++;
                    continue;
                }
            }
            result.append(current);
        }
        return result.toString();
    }

    private String removeBom(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }
}
