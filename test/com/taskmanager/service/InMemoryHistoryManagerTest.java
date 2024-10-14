package com.taskmanager.service;

import com.taskmanager.model.Epic;
import com.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void prepare() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void getHistoryLess10Added() {
        Epic epic = new Epic("testEpic");
        Task task = new Task("testTask");
        historyManager.add(epic);
        historyManager.add(task);

        assertEquals(historyManager.getHistory().size(), 2, "Ожидалась список из 2 элементов");
        assertTrue(historyManager.getHistory().containsAll(List.of(epic, task)), "Не обнаружены добавленные в историю элемент");
    }

    @Test
    public void getHistoryMore10Added() {
        List<Task> tasks = new ArrayList<>();
        int taskCount = 15;
        for (int i = 0; i < taskCount; i++) {
            Task tempTask = new Task("task" + i);
            tasks.add(tempTask);
            historyManager.add(tempTask);
        }

        tasks = tasks.subList(taskCount - InMemoryHistoryManager.getHistoryCapacity(), taskCount);

        assertEquals(historyManager.getHistory().size(), InMemoryHistoryManager.getHistoryCapacity(), "Ожидался список из 10 элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не совпадают ожидаемый и фактический список задач в истории");
    }

}