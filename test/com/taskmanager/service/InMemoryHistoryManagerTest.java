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
    public void checkGetHistory() {
        Epic epic = new Epic(1, "testEpic");
        Task task = new Task(2, "testTask");
        historyManager.add(epic);
        historyManager.add(task);

        assertEquals(historyManager.getHistory().size(), 2, "Ожидалась список из 2 элементов");
        assertTrue(historyManager.getHistory().containsAll(List.of(epic, task)), "Не обнаружены добавленные в историю элемент");
    }

    @Test
    void checkAddToHistoryOneTask() {
        Epic epic = new Epic(1, "testEpic");
        historyManager.add(epic);

        assertEquals(historyManager.getHistory().size(), 1, "Ожидался список из 1 элемента");
        assertTrue(historyManager.getHistory().contains(epic), "Не обнаружен добавленный в историю элемент");
    }

    @Test
    void checkAddToHistoryManyTasks() {
        List<Task> tasks = List.of(new Task(1, "testTask1"), new Task(2, "testTask2"), new Task(3, "testTask3"));

        historyManager.add(tasks.get(0));
        historyManager.add(tasks.get(1));
        historyManager.add(tasks.get(2));

        assertEquals(historyManager.getHistory().size(), tasks.size(), "Ожидался список из " + tasks.size() + " элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не обнаружены добавленные в историю элементы");
        assertEquals(historyManager.getHistory().getFirst(), tasks.get(0), "Ожидался другой первый элемент");
        assertEquals(historyManager.getHistory().getLast(), tasks.get(2), "Ожидался другой последний элемент");
    }

    @Test
    void checkAddToHistoryManyTasksManyTimes() {
        List<Task> tasks = List.of(new Task(1, "testTask1"), new Task(2, "testTask2"), new Task(3, "testTask3"));

        historyManager.add(tasks.get(0));
        historyManager.add(tasks.get(1));
        historyManager.add(tasks.get(2));
        historyManager.add(tasks.get(1));
        historyManager.add(tasks.get(0));

        assertEquals(historyManager.getHistory().size(), tasks.size(), "Ожидался список из " + tasks.size() + " элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не обнаружены добавленные в историю элементы");
        assertEquals(historyManager.getHistory().getFirst(), tasks.get(2), "Ожидался другой первый элемент");
        assertEquals(historyManager.getHistory().getLast(), tasks.get(0), "Ожидался другой последний элемент");
    }

    @Test
    void checkDeleteFirstElementInHistory() {
        Task task1 = new Task(1, "testTask1");
        Task task2 = new Task(2, "testTask2");
        Task task3 = new Task(3, "testTask3");
        Task task4 = new Task(4, "testTask4");
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3, task4));

        historyManager.add(task4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        tasks.remove(task4);
        historyManager.remove(4);

        assertEquals(historyManager.getHistory().size(), tasks.size(), "Ожидался список из " + tasks.size() + " элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не обнаружены добавленные в историю элементы");
        assertEquals(historyManager.getHistory().getFirst(), task1, "Ожидался другой первый элемент");
        assertEquals(historyManager.getHistory().getLast(), task3, "Ожидался другой последний элемент");
    }

    @Test
    void checkDeleteLastElementInHistory() {
        Task task1 = new Task(1, "testTask1");
        Task task2 = new Task(2, "testTask2");
        Task task3 = new Task(3, "testTask3");
        Task task4 = new Task(4, "testTask4");
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3, task4));

        historyManager.add(task4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        tasks.remove(task3);
        historyManager.remove(3);

        assertEquals(historyManager.getHistory().size(), tasks.size(), "Ожидался список из " + tasks.size() + " элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не обнаружены добавленные в историю элементы");
        assertEquals(historyManager.getHistory().getFirst(), task4, "Ожидался другой первый элемент");
        assertEquals(historyManager.getHistory().getLast(), task2, "Ожидался другой последний элемент");
    }

    @Test
    void checkDeleteMiddleElementInHistory() {
        Task task1 = new Task(1, "testTask1");
        Task task2 = new Task(2, "testTask2");
        Task task3 = new Task(3, "testTask3");
        Task task4 = new Task(4, "testTask4");
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3, task4));

        historyManager.add(task4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        tasks.remove(task1);
        historyManager.remove(1);

        assertEquals(historyManager.getHistory().size(), tasks.size(), "Ожидался список из " + tasks.size() + " элементов");
        assertTrue(historyManager.getHistory().containsAll(tasks), "Не обнаружены добавленные в историю элементы");
        assertEquals(historyManager.getHistory().getFirst(), task4, "Ожидался другой первый элемент");
        assertEquals(historyManager.getHistory().getLast(), task3, "Ожидался другой последний элемент");
        assertEquals(historyManager.getHistory().get(1), task2, "Ожидался другой последний элемент");
    }


}