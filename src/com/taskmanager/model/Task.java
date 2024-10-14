package com.taskmanager.model;

import java.util.Objects;

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
     * @param id          Идентификатор
     * @param name        Наименование
     * @param description Описание
     */
    public Task(int id, String name, String description) {
        super(id, name, description);
    }

    /**
     * Представление объекта задачи в виде строки
     *
     * @return Строка, описывающая объект задачи
     */
    @Override
    public String toString() {
        return Task.class.getName() + " { " +
                "id = " + getId() + ", " +
                "name = \"" + getName() + "\", " +
                "description = \"" + getDescription() + "\", " +
                "status = " + getStatus() +
                " }";
    }
}
