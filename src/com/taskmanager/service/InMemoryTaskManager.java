package com.taskmanager.service;

import com.taskmanager.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Класс таск-менеджера, реализующий хранение задач в текущей памяти
 */
public class InMemoryTaskManager implements TaskManager {
    /**
     * Текущий свободный идентификтор
     */
    private int currentId;
    /**
     * Список задач
     */
    private final HashMap<Integer, Task> tasks;
    /**
     * Список эпиков
     */
    private final HashMap<Integer, Epic> epics;
    /**
     * Список подзадач
     */
    private final HashMap<Integer, Subtask> subtasks;
    /**
     * Менеджер для работы с историей просмотра
     */
    private final HistoryManager historyManager;

    /**
     * Конструктор для создания нового таск-менеджера
     */
    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.currentId = 1;
    }

    @Override
    public void addTask(Task task) {
        if (tasks.containsValue(task)) {
            System.out.println("Такая задача уже была добавлена");
            return;
        }

        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            System.out.println("Такой эпик уже был добавлен");
            return;
        }

        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {
            if (Objects.equals(subtasks.values().stream().filter(item -> item.equals(subtask)).findFirst().orElseThrow().getEpicId(), subtask.getEpicId())) {
                System.out.println("Такая подзадача в эпик с id = " + subtask.getEpicId() + " уже была добавлена");
                return;
            }
        }

        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);

        epics.get(subtask.getEpicId()).addNewSubtask(subtask.getId());
        correctEpicStatus(subtask.getEpicId());
    }

    @Override
    public void editTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Передана задача без id. Невозможно обновить");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void editEpic(Epic epic) {
        if (epic.getId() == null) {
            System.out.println("Передан эпик без id. Невозможно обновить");
            return;
        }

        final Epic epicSaved = epics.get(epic.getId());
        epicSaved.setName(epic.getName());
        epicSaved.setDescription(epic.getDescription());
    }

    @Override
    public void editSubtask(Subtask subtask) {
        if (subtask.getId() == null || subtask.getEpicId() == null) {
            System.out.println("Передана подзадача без id или без привязки к эпику. Невозможно обновить");
            return;
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Передана подзадача с привязкой к несуществующему эпику. Невозможно обновить");
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        correctEpicStatus(subtask.getEpicId());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<AbstractTask> getAllEntities() {
        ArrayList<AbstractTask> allEntities = new ArrayList<>();

        allEntities.addAll(tasks.values());
        allEntities.addAll(epics.values());
        allEntities.addAll(subtasks.values());

        return allEntities;
    }

    @Override
    public List<Subtask> getSubtaskListByEpicId(Integer epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Не существует эпика с id = " + epicId);
            return null;
        }

        return epic.getSubtaskList().stream().map(subtasks::get).toList();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        epics.forEach((id, epic) -> {
            epic.deleteAllSubtasks();
            correctEpicStatus(epic.getId());
        });
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksInEpic(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Не существует эпика с id = " + epicId);
            return;
        }

        final List<Integer> subtaskIds = epic.getSubtaskList();
        subtaskIds.forEach(subtasks::remove);
        epic.deleteAllSubtasks();
        correctEpicStatus(epicId);
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Не существует задачи с id = " + id);
            return null;
        }

        historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);

        if (epic == null) {
            System.out.println("Не существует эпика с id = " + id);
            return null;
        }

        historyManager.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            System.out.println("Не существует подзадачи с id = " + id);
            return null;
        }

        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        final Task task = tasks.remove(id);
        if (task == null) {
            System.out.println("Не существует задачи с id = " + id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Не существует эпика с id = " + id);
            return;
        }

        final List<Integer> subtaskIds = epic.getSubtaskList();
        subtaskIds.forEach(subtasks::remove);
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Не существует подзадачи с id = " + id);
            return;
        }

        int epicId = subtask.getEpicId();
        epics.get(epicId).deleteSubtaskById(id);
        correctEpicStatus(epicId);
    }

    @Override
    public int getNextId() {
        return currentId++;
    }

    @Override
    public void correctEpicStatus(int epicId) {
        int countToDo = 0;
        int countDone = 0;
        final Epic epic = epics.get(epicId);
        final List<Integer> currentSubtaskList = epic.getSubtaskList();

        for (Integer id : currentSubtaskList) {
            switch (subtasks.get(id).getStatus()) {
                case NEW -> countToDo++;
                case DONE -> countDone++;
                default -> {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }

        int subtaskListSize = currentSubtaskList.size();
        if (countToDo == subtaskListSize) {
            epic.setStatus(Status.NEW);
        } else if (countDone == subtaskListSize) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return Objects.equals(tasks, that.tasks)
                && Objects.equals(epics, that.epics)
                && Objects.equals(subtasks, that.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epics, subtasks);
    }

    /**
     * Представление объекта таск-менеджера в виде строки
     *
     * @return Строка, описывающая объект таск-менеджера
     */
    @Override
    public String toString() {
        return InMemoryTaskManager.class.getName() + " { " +
                "currentId = " + currentId + ", " +
                "taskList = " + tasks + ", " +
                "epicList = " + epics + ", " +
                "subtaskList = " + subtasks +
                " }";
    }
}
