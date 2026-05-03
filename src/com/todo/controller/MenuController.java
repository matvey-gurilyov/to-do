package com.todo.controller;

import com.todo.exception.PersistenceException;
import com.todo.exception.TaskNotFoundException;
import com.todo.exception.ValidationException;
import com.todo.model.Task;
import com.todo.model.TaskStatus;
import com.todo.service.TaskService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

public class MenuController {
    private final TaskService taskService;
    private final BufferedReader reader;
    private final PrintStream out;

    public MenuController(TaskService taskService, Charset consoleCharset) {
        this.taskService = taskService;
        this.reader = new BufferedReader(new InputStreamReader(System.in, consoleCharset));
        this.out = new PrintStream(System.out, true, consoleCharset);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int option = readInt("Choose option: ");
            try {
                switch (option) {
                    case 1:
                        createTask();
                        break;
                    case 2:
                        printAllTasks();
                        break;
                    case 3:
                        printTaskById();
                        break;
                    case 4:
                        updateTask();
                        break;
                    case 5:
                        deleteTask();
                        break;
                    case 6:
                        changeStatus();
                        break;
                    case 7:
                        running = false;
                        out.println("Goodbye!");
                        break;
                    default:
                        out.println("Unknown option. Try again.");
                }
            } catch (ValidationException | TaskNotFoundException | PersistenceException e) {
                out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                out.println("Unexpected error: " + e.getMessage());
            }
            out.println();
        }
    }

    private void printMenu() {
        out.println("=== TODO APP ===");
        out.println("1. Create task");
        out.println("2. Get all tasks");
        out.println("3. Get task by id");
        out.println("4. Update task");
        out.println("5. Delete task");
        out.println("6. Change task status");
        out.println("7. Exit");
    }

    private void createTask() {
        String title = readRequiredLine("Title: ");
        String description = readOptionalLine("Description: ");
        Task task = taskService.createTask(title, description);
        out.println("Task created: " + formatTask(task));
    }

    private void printAllTasks() {
        out.print("Filter by status? (TODO/IN_PROGRESS/DONE or ENTER to skip): ");
        String rawStatus = readLine().trim();
        List<Task> tasks;
        if (rawStatus.isEmpty()) {
            tasks = taskService.getAllTasks();
        } else {
            TaskStatus status = parseStatus(rawStatus);
            tasks = taskService.getTasksByStatus(status);
        }

        if (tasks.isEmpty()) {
            out.println("No tasks found.");
            return;
        }
        tasks.forEach(task -> out.println(formatTask(task)));
    }

    private void printTaskById() {
        long id = readLong("Task id: ");
        Task task = taskService.getTaskById(id);
        out.println(formatTask(task));
    }

    private void updateTask() {
        long id = readLong("Task id: ");
        String title = readRequiredLine("New title: ");
        String description = readOptionalLine("New description: ");
        Task updated = taskService.updateTask(id, title, description);
        out.println("Task updated: " + formatTask(updated));
    }

    private void deleteTask() {
        long id = readLong("Task id: ");
        taskService.deleteTask(id);
        out.println("Task deleted.");
    }

    private void changeStatus() {
        long id = readLong("Task id: ");
        TaskStatus newStatus = parseStatus(readRequiredLine("New status (TODO/IN_PROGRESS/DONE): "));
        Task updated = taskService.changeTaskStatus(id, newStatus);
        out.println("Status changed: " + formatTask(updated));
    }

    private int readInt(String prompt) {
        while (true) {
            out.print(prompt);
            String raw = readLine();
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                out.println("Invalid number. Try again.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            out.print(prompt);
            String raw = readLine();
            try {
                return Long.parseLong(raw.trim());
            } catch (NumberFormatException e) {
                out.println("Invalid id. Try again.");
            }
        }
    }

    private String readRequiredLine(String prompt) {
        while (true) {
            out.print(prompt);
            String value = readLine();
            if (!value.trim().isEmpty()) {
                return value;
            }
            out.println("Value cannot be empty.");
        }
    }

    private String readOptionalLine(String prompt) {
        out.print(prompt);
        return readLine();
    }

    private TaskStatus parseStatus(String rawStatus) {
        try {
            return TaskStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown status. Available: TODO, IN_PROGRESS, DONE.");
        }
    }

    private String formatTask(Task task) {
        return String.format(
                "[id=%d] %s | %s | status=%s | createdAt=%s",
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }

    private String readLine() {
        try {
            String line = reader.readLine();
            return line == null ? "" : line;
        } catch (IOException e) {
            throw new ValidationException("Failed to read input from console.");
        }
    }
}
