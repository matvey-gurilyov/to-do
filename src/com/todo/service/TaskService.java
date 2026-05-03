package com.todo.service;

import com.todo.exception.TaskNotFoundException;
import com.todo.exception.ValidationException;
import com.todo.model.Task;
import com.todo.model.TaskStatus;
import com.todo.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description) {
        validateTitle(title);
        String safeDescription = description == null ? "" : description.trim();

        Task task = new Task(
                taskRepository.getNextId(),
                title.trim(),
                safeDescription,
                TaskStatus.TODO,
                LocalDateTime.now()
        );
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found."));
    }

    public Task updateTask(long id, String newTitle, String newDescription) {
        validateTitle(newTitle);
        Task task = getTaskById(id);
        task.setTitle(newTitle.trim());
        task.setDescription(newDescription == null ? "" : newDescription.trim());
        return taskRepository.update(task);
    }

    public void deleteTask(long id) {
        if (!taskRepository.deleteById(id)) {
            throw new TaskNotFoundException("Task with id " + id + " not found.");
        }
    }

    public Task changeTaskStatus(long id, TaskStatus status) {
        if (status == null) {
            throw new ValidationException("Status cannot be null.");
        }
        Task task = getTaskById(id);
        task.setStatus(status);
        return taskRepository.update(task);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty.");
        }
    }
}
