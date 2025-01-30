package com.taskmanager.service;

import com.taskmanager.model.*;

import java.util.*;

/**
 * Интерфейс, обеспечивающий работу с задачами
 */
public interface TaskManager {

    /**
     * Добавление задачи
     *
     * @param task Объект задачи
     */
    void addTask(Task task);

    /**
     * Добавление эпика
     *
     * @param epic Объект эпика
     */
    void addEpic(Epic epic);

    /**
     * Добавление подзадачи
     *
     * @param subtask Объект подзадачи
     */
    void addSubtask(Subtask subtask);

    /**
     * Редактирование задачи
     *
     * @param task Обновлённая версия добавленной ранее задачи
     */
    void editTask(Task task);

    /**
     * Редактирование эпика
     *
     * @param epic Обновлённая версия добавленного ранее эпика
     */
    void editEpic(Epic epic);

    /**
     * Редактирование подзадачи
     *
     * @param subtask Обновлённая версия добавленной ранее подзадачи
     */
    void editSubtask(Subtask subtask);

    /**
     * Получение списка задач
     *
     * @return Список задач
     */
    List<Task> getTasks();

    /**
     * Получение списка эпика
     *
     * @return Список эпика
     */
    List<Epic> getEpics();

    /**
     * Получение списка подзадач
     *
     * @return Список подзадач
     */
    List<Subtask> getSubtasks();

    /**
     * Получение всех задач, эпиков и подзадач
     *
     * @return Список всех задач, эпиков и подзадач
     */
    List<AbstractTask> getAllEntities();

    /**
     * Получение списка подзадач конкретного эпика
     *
     * @param epicId Идентификатор эпика
     * @return Список подзадач
     */
    List<Subtask> getSubtaskListByEpicId(Integer epicId);

    /**
     * Удаление всех задач
     */
    void deleteAllTasks();

    /**
     * Удаление всех эпиков
     */
    void deleteAllEpics();

    /**
     * Удаление всех подзадач
     */
    void deleteAllSubtasks();

    /**
     * Удаление подзадачи в конкретном эпике
     *
     * @param epicId Идентификатор эпика
     */
    void deleteAllSubtasksInEpic(int epicId);

    /**
     * Получение задачи по идентификатору
     *
     * @param id Идентификатор
     * @return Задача
     */
    Task getTaskById(int id);

    /**
     * Получение эпика по идентификатору
     *
     * @param id Идентификатор
     * @return Эпик
     */
    Epic getEpicById(int id);

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id Идентификатор
     * @return Подзадача
     */
    Subtask getSubtaskById(int id);

    /**
     * Удаление конкретной задачи
     *
     * @param id Идентификатор задачи
     */
    void deleteTaskById(int id);

    /**
     * Удаление конкретного эпика
     *
     * @param id Идентификатор эпика
     */
    void deleteEpicById(int id);

    /**
     * Удаление конкретной подзадачи
     *
     * @param id Идентификатор подзадачи
     */
    void deleteSubtaskById(int id);

    /**
     * Получение номера (идентификатора) для задачи
     *
     * @return Идентификатор
     */
    int getNextId();

    /**
     * Корректировка статуса эпика
     *
     * @param epicId Идентификатор эпика
     */
    void correctEpicStatus(int epicId);

    void correctEpicDuration(Epic epic);

    /**
     * Получение истории просмотра задач (последние 10 задач)
     *
     * @return Список задач
     */
    List<AbstractTask> getHistory();
}
