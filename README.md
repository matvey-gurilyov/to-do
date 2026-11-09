# Todo List (Console CRUD App, Java Core)

An educational console application for managing tasks, with data persisted to a file between runs.

## Features

- Create a task
- View all tasks
- View a task by ID
- Update a task
- Delete a task
- Change a task's status
- Save and load tasks from the file `data/tasks.txt`
- Filter tasks by status (Streams API)
- Sort tasks by creation date

## Structure

- `model` — `Task`, `TaskStatus`
- `repository` — `TaskRepository`, `FileTaskRepository`
- `service` — `TaskService`
- `controller` — `MenuController`
- `util` — `FileUtil`, `IdGenerator`, `DateUtil`
- `exception` — custom exceptions

## Running (PowerShell)

```powershell
mkdir out -ErrorAction SilentlyContinue
javac -d out src/com/todo/**/*.java src/com/todo/*.java
java -cp out com.todo.Main
```

## Storage Format

Each task is stored on a single line:

`id|title|description|status|createdAt`

Example:

`1|Buy milk|2 liters for tomorrow|TODO|2026-05-03T19:00:00`
