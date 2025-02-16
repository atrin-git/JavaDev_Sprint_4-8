package com.taskmanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Дата-класс для эпика
 */
public class Epic extends AbstractTask {
    /**
     * Список подзадач эпика
     */
    private final List<Integer> subtaskList;

    /**
     * Дата и время окончания работы над эпиком
     */
    private LocalDateTime endTime;

    /**
     * Конструктор копирования
     *
     * @param epic Объект, для которого требуется создать копию
     */
    public Epic(Epic epic) {
        super(epic);
        this.subtaskList = new ArrayList<>(epic.getSubtaskList());
        this.endTime = epic.getEndTime();
    }

    /**
     * Конструктор
     *
     * @param name Наименование
     */
    public Epic(String name) {
        super(name);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param name        Наименование
     * @param description Описание
     */
    public Epic(String name, String description) {
        super(name, description);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param id   Идентификатор
     * @param name Наименование
     */
    public Epic(Integer id, String name) {
        super(id, name);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param id          Идентификатор
     * @param name        Наименование
     * @param description Описание
     */
    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Получение списка подзадач
     *
     * @return Список подзадач
     */
    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

    /**
     * Добавление новой подзадачи в список
     *
     * @param id Идентификатор подзадачи
     */
    public void addNewSubtask(int id) {
        subtaskList.add(id);
    }

    /**
     * Удаление подзадачи по идентификатору
     *
     * @param id Идентификатор
     */
    public void deleteSubtaskById(Integer id) {
        subtaskList.remove(id);
    }

    /**
     * Удаление всех подзадач из эпика
     */
    public void deleteAllSubtasks() {
        subtaskList.clear();
    }

    /**
     * Получить дату и время окончания работы над эпиком
     *
     * @return Дата и время окончания работы над эпиком
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * Установить дату и время окончания работы над эпиком
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Представление объекта эпика в виде строки
     *
     * @return Строка, описывающая объект эпика
     */
    @Override
    public String toString() {
        return String.join(",",
                /* id */            getId().toString(),
                /* type */          TaskType.EPIC.toString(),
                /* name */          getName(),
                /* status */        getStatus().toString(),
                /* description */   getDescription() != null ? getDescription() : "",
                /* epic */          "",
                /* start_date */    getStartTime() != null ? getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                /* duration */      String.valueOf(getDuration().toSeconds()));
    }
}
