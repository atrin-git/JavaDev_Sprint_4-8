package com.taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Дата-класс для задачи
 */
public class Task extends AbstractTask {

    /**
     * Конструктор копирования
     *
     * @param task Объект задачи, для которого требуется копия
     */
    public Task(Task task) {
        super(task);
    }

    /**
     * Конструктор
     *
     * @param name Наименование задачи
     */
    public Task(String name) {
        super(name);
    }

    /**
     * Конструктор
     *
     * @param name        Наименование
     * @param description Описание
     */
    public Task(String name, String description) {
        super(name, description);
    }

    /**
     * Конструктор
     *
     * @param id   Идентификатор
     * @param name Наименование
     */
    public Task(int id, String name) {
        super(id, name);
    }

    /**
     * Конструктор
     *
     * @param id        Идентификатор
     * @param name      Наименование
     * @param startTime Дата и время начала работы над задачей
     * @param duration  Продолжительность работы с задачей
     */
    public Task(int id, String name, LocalDateTime startTime, Duration duration) {
        super(id, name, startTime, duration);
    }

    /**
     * Конструктор
     *
     * @param id          Идентификатор
     * @param name        Наименование
     * @param description Описание
     */
    public Task(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Конструктор
     *
     * @param id          Идентификатор
     * @param name        Наименование
     * @param description Описание
     * @param startTime   Дата и время начала работы над задачей
     * @param duration    Продолжительность работы с задачей
     */
    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
    }

    /**
     * Представление объекта задачи в виде строки
     *
     * @return Строка, описывающая объект задачи
     */
    @Override
    public String toString() {
        return String.join(",",
                /* id */            getId().toString(),
                /* type */          TaskType.TASK.toString(),
                /* name */          getName(),
                /* status */        getStatus().toString(),
                /* description */   getDescription() != null ? getDescription() : "",
                /* epic */          "",
                /* start_date */    getStartTime() != null ? getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                /* duration */      String.valueOf(getDuration().toSeconds()));
    }
}
