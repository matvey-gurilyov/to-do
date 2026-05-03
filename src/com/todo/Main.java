package com.todo;

import com.todo.controller.MenuController;
import com.todo.repository.FileTaskRepository;
import com.todo.repository.TaskRepository;
import com.todo.service.TaskService;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Charset consoleCharset = resolveConsoleCharset();

        Path storagePath = Path.of("data", "tasks.txt");
        TaskRepository taskRepository = new FileTaskRepository(storagePath);
        TaskService taskService = new TaskService(taskRepository);
        MenuController menuController = new MenuController(taskService, consoleCharset);
        menuController.start();
    }

    private static Charset resolveConsoleCharset() {
        String[] candidates = {
                System.getProperty("stdout.encoding"),
                System.getProperty("sun.stdout.encoding"),
                System.getProperty("native.encoding")
        };
        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            try {
                return Charset.forName(candidate.trim());
            } catch (UnsupportedCharsetException | IllegalCharsetNameException ignored) {
                // Ignore invalid platform property and try the next one.
            }
        }
        return Charset.defaultCharset();
    }
}
