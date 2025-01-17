package com.taskmanager.service;

import com.taskmanager.model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private FileBackedTaskManager() {
        this.file = null;
    }

    public FileBackedTaskManager(File file) {
        if (file == null) {
            throw new ManagerReadException("Файл не инициализирован");
        }

        this.file = file;
        restoreFromFile();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void editTask(Task task) {
        super.editTask(task);
        save();
    }

    @Override
    public void editEpic(Epic epic) {
        super.editEpic(epic);
        save();
    }

    @Override
    public void editSubtask(Subtask subtask) {
        super.editSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksInEpic(int epicId) {
        super.deleteAllSubtasksInEpic(epicId);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    private void addAbstractTask(AbstractTask abstractTask) {
        switch (TaskType.valueOf(abstractTask.getClass().getSimpleName().toUpperCase())) {
            case EPIC -> {
                super.addEpic((Epic) abstractTask);
            }
            case TASK -> {
                super.addTask((Task) abstractTask);
            }
            case SUBTASK -> {
                super.addSubtask((Subtask) abstractTask);
            }
        }
    }

    private void restoreFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // пропускаем шапку
            while ((line = reader.readLine()) != null) {
                AbstractTask task = fromString(line);
                if (task == null) {
                    throw new ManagerReadException("Не удалось преобразовать строку в задачу: " + line);
                }

                addAbstractTask(task);
            }
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка при чтении данных из файла: " + e.getMessage());
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных в файл: " + e.getMessage());
        }
    }

    private AbstractTask fromString(String str) {
        final String[] words = str.split(",", -1);

        try {
            final TaskType taskType = TaskType.valueOf(words[1]);
            switch (taskType) {
                case TASK -> {
                    Task task = new Task(Integer.parseInt(words[0]), words[2], words[4]);
                    task.setStatus(Status.valueOf(words[3]));
                    return task;
                }
                case EPIC -> {
                    Epic epic = new Epic(Integer.parseInt(words[0]), words[2], words[4]);
                    epic.setStatus(Status.valueOf(words[3]));
                    return epic;
                }
                case SUBTASK -> {
                    Subtask subtask = new Subtask(Integer.parseInt(words[0]), words[2], words[4], Integer.parseInt(words[5]));
                    subtask.setStatus(Status.valueOf(words[3]));
                    return subtask;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
