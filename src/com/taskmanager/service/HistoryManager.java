package com.taskmanager.service;

import com.taskmanager.model.AbstractTask;

import java.util.List;

public interface HistoryManager {

    /**
     * Добавление задачи в список просмотренных
     * @param abstractTask Задача
     */
    void add(AbstractTask abstractTask);

    /**
     * Получение списка просмотренных задача
     * @return Список просмотренных задач
     */
    List<AbstractTask> getHistory();
}
