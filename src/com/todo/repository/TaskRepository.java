package com.todo.repository;

import com.todo.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);

    List<Task> findAll();

    Optional<Task> findById(long id);

    Task update(Task task);

    boolean deleteById(long id);

    long getNextId();
}
