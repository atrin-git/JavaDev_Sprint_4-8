package com.taskmanager.model;

import java.util.Objects;

/**
 * Абстрактный класс, описывающий сущность задачи
 */
public abstract class AbstractTask {
    /**
     * Уникальный идентификатор задачи
     */
    private Integer id;
    /**
     * Наименование задачи
     */
    private String name;
    /**
     * Описание задачи
     */
    private String description;
    /**
     * Статус задачи
     */
    private Status status;

    /**
     * Контруктор копирования
     *
     * @param abstractTask Объект, для которого необходима копия
     */
    public AbstractTask(AbstractTask abstractTask) {
        this.id = abstractTask.getId();
        this.name = abstractTask.getName();
        this.description = abstractTask.getDescription();
        this.status = abstractTask.getStatus();
    }

    /**
     * Конструктор
     *
     * @param id          Идентификатор
     * @param name        Имя
     * @param description Описание
     */
    public AbstractTask(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    /**
     * Конструктор
     *
     * @param name Имя
     */
    public AbstractTask(String name) {
        this(null, name, null);
    }

    /**
     * Конструктор
     *
     * @param name        Имя
     * @param description Описание
     */
    public AbstractTask(String name, String description) {
        this(null, name, description);
    }

    /**
     * Конструктор
     *
     * @param id   Идентификатор
     * @param name Имя
     */
    public AbstractTask(int id, String name) {
        this(id, name, null);
    }

    /**
     * Получение идентификатора
     *
     * @return Идентификатор
     */
    public Integer getId() {
        return id;
    }

    /**
     * Установка идентификатора
     *
     * @param id Идентификатор
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Получение наименования
     *
     * @return Наименование
     */
    public String getName() {
        return name;
    }

    /**
     * Установка наименования
     *
     * @param name Наименование
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получение описания
     *
     * @return Описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Установка описания
     *
     * @param description Описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Получение статуса
     *
     * @return Статус
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Установка статуса
     *
     * @param status Статус
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Метод првоерк задач на эквивалентность
     *
     * @param o Задача для сравнения
     * @return Результат сравнения
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask that = (AbstractTask) o;
        if (this.getId() != null && this.getId().equals(that.getId())) return true;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    /**
     * Получение хэш-кода объекта
     *
     * @return Хэш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

}
