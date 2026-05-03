package com.todo.util;

import com.todo.exception.PersistenceException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class FileUtil {
    private FileUtil() {
    }

    public static List<String> readAllLines(Path path) {
        try {
            if (!Files.exists(path)) {
                return List.of();
            }
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content.isEmpty()) {
                return List.of();
            }
            return Arrays.asList(content.split("\\R", -1));
        } catch (IOException e) {
            throw new PersistenceException("Failed to read file: " + path, e);
        }
    }

    public static void writeLines(Path path, List<String> lines) {
        try {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            String content = String.join(System.lineSeparator(), lines);
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenceException("Failed to write file: " + path, e);
        }
    }
}
