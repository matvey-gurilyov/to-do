# Todo List (Console CRUD App, Java Core)

Учебное консольное приложение для управления задачами с сохранением данных в файл между запусками.

## Что реализовано

- Создание задачи
- Просмотр всех задач
- Просмотр задачи по ID
- Обновление задачи
- Удаление задачи
- Изменение статуса задачи
- Сохранение и загрузка задач из файла `data/tasks.txt`
- Фильтрация задач по статусу (Streams API)
- Сортировка задач по дате создания

## Структура

- `model` — `Task`, `TaskStatus`
- `repository` — `TaskRepository`, `FileTaskRepository`
- `service` — `TaskService`
- `controller` — `MenuController`
- `util` — `FileUtil`, `IdGenerator`, `DateUtil`
- `exception` — кастомные исключения

## Запуск (PowerShell)

```powershell
mkdir out -ErrorAction SilentlyContinue
javac -d out src/com/todo/**/*.java src/com/todo/*.java
java -cp out com.todo.Main
```

Если в терминале неверная кодовая страница, переключите ее в UTF-8:

```powershell
chcp 65001
```

Для полной очистки старого поврежденного файла (если видите старые `????`/`я┐╜`) можно один раз запустить с reset-флагом:

```powershell
java -Dtodo.storage.reset=true -cp out com.todo.Main
```

## Формат хранения

Каждая задача хранится одной строкой:

`id|title|description|status|createdAt`

Пример:

`1|Buy milk|2 liters for завтра|TODO|2026-05-03T19:00:00`
