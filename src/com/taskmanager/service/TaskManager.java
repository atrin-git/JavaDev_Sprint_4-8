package com.taskmanager.service;

import com.taskmanager.model.*;

import java.util.*;

/**
 * Класс, обеспечивающий работу таск-менеджер с задачами
 */
public class TaskManager {
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
     * Конструктор для создания нового таск-менеджера
     */
    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.currentId = 1;
    }

    /**
     * Добавление задачи
     *
     * @param task Объект задачи
     */
    public void addTask(Task task) {
        if (tasks.containsValue(task)) {
            System.out.println("Такая задача уже была добавлена");
            return;
        }

        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    /**
     * Добавление эпика
     *
     * @param epic Объект эпика
     */
    public void addEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            System.out.println("Такой эпик уже был добавлен");
            return;
        }

        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    /**
     * Добавление подзадачи
     *
     * @param subtask Объект подзадачи
     */
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

    /**
     * Редактирование задачи
     *
     * @param task Обновлённая версия добавленной ранее задачи
     */
    public void editTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Передана задача без id. Невозможно обновить");
            return;
        }
        tasks.put(task.getId(), task);
    }

    /**
     * Редактирование эпика
     *
     * @param epic Обновлённая версия добавленного ранее эпика
     */
    public void editEpic(Epic epic) {
        if (epic.getId() == null) {
            System.out.println("Передан эпик без id. Невозможно обновить");
            return;
        }

        final Epic epicSaved = epics.get(epic.getId());
        epicSaved.setName(epic.getName());
        epicSaved.setDescription(epic.getDescription());

        epics.put(epic.getId(), epicSaved);
    }

    /**
     * Редактирование подзадачи
     *
     * @param subtask Обновлённая версия добавленной ранее подзадачи
     */
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

    /**
     * Получение списка задач
     *
     * @return Список задач
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Получение списка эпика
     *
     * @return Список эпика
     */
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Получение списка подзадач
     *
     * @return Список подзадач
     */
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Получение всех задач, эпиков и подзадач
     *
     * @return Список всех задач, эпиков и подзадач
     */
    public ArrayList<AbstractTask> getAllEntities() {
        ArrayList<AbstractTask> allEntities = new ArrayList<>();

        allEntities.addAll(tasks.values());
        allEntities.addAll(epics.values());
        allEntities.addAll(subtasks.values());

        return allEntities;
    }

    /**
     * Получение списка подзадач конкретного эпика
     *
     * @param epicId Идентификатор эпика
     * @return Список подзадач
     */
    public List<Subtask> getSubtaskListByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Не существует эпика с id = " + epicId);
            return null;
        }

        return epics.get(epicId).getSubtaskList().stream().map(subtasks::get).toList();
    }

    /**
     * Удаление всех задач
     */
    public void deleteAllTasks() {
        tasks.clear();
    }

    /**
     * Удаление всех эпиков
     */
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    /**
     * Удаление всех подзадач
     */
    public void deleteAllSubtasks() {
        epics.forEach((id, epic) -> {
            epic.deleteAllSubtasks();
            correctEpicStatus(epic.getId());
        });
        subtasks.clear();
    }

    /**
     * Удаление подзадачи в конкретном эпике
     *
     * @param epicId Идентификатор эпика
     */
    public void deleteAllSubtasksInEpic(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Не существует эпика с id = " + epicId);
            return;
        }

        final List<Integer> subtaskIds = epic.getSubtaskList();
        subtaskIds.forEach(subtasks::remove);
        subtaskIds.clear();
    }

    /**
     * Получение задачи по индентификатору
     *
     * @param id Идентификатор
     * @return Задача
     */
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Не существует задачи с id = " + id);
            return null;
        }

        return task;
    }

    /**
     * Получение эпика по идентификатору
     *
     * @param id Идентификатор
     * @return Эпик
     */
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);

        if (epic == null) {
            System.out.println("Не существует эпика с id = " + id);
            return null;
        }

        return epic;
    }

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id Идентификатор
     * @return Подзадача
     */
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            System.out.println("Не существует подзадачи с id = " + id);
            return null;
        }

        return subtask;
    }

    /**
     * Удаление конкретной задачи
     *
     * @param id Идентификатор задачи
     */
    public void deleteTaskById(int id) {
        final Task task = tasks.remove(id);
        if (task == null) {
            System.out.println("Не существует задачи с id = " + id);
        }
    }

    /**
     * Удаление конкретного эпика
     *
     * @param id Идентификатор эпика
     */
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Не существует эпика с id = " + id);
            return;
        }

        final List<Integer> subtaskIds = epic.getSubtaskList();
        subtaskIds.forEach(subtasks::remove);
    }

    /**
     * Удаление конкретной подзадачи
     *
     * @param id Идентификатор подзадачи
     */
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Не существует подзадачи с id = " + id);
            return;
        }

        int epicId = subtask.getEpicId();
        epics.get(epicId).deleteSubtaskById(id);
    }

    /**
     * Получение номера (идентификатора) для задачи
     *
     * @return Идентификтор
     */
    private int getNextId() {
        return currentId++;
    }

    /**
     * Корректировка статуса эпика
     *
     * @param epicId Идентификатор эпика
     */
    private void correctEpicStatus(int epicId) {
        int countToDo = 0;
        int countDone = 0;
        final List<Integer> currentSubtaskList = epics.get(epicId).getSubtaskList();

        for (Integer id : currentSubtaskList) {
            switch (subtasks.get(id).getStatus()) {
                case NEW -> countToDo++;
                case DONE -> countDone++;
                default -> {
                    epics.get(epicId).setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }

        int subtaskListSize = currentSubtaskList.size();
        if (countToDo == subtaskListSize) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (countDone == subtaskListSize) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    /**
     * Представление объекта таск-менеджера в виде строки
     *
     * @return Строка, описывающая объект таск-менеджера
     */
    @Override
    public String toString() {
        return "com.taskmanager.service.TaskManager { " +
                "currentId = " + currentId + ", " +
                "taskList = " + tasks + ", " +
                "epicList = " + epics + ", " +
                "subtaskList = " + subtasks +
                " }";
    }
}
