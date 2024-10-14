package com.taskmanager.model;

import com.taskmanager.service.InMemoryTaskManager;
import com.taskmanager.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void checkTasksEqualsByAllFields() {
        String name = "name";
        String description = "description";
        Status status = Status.IN_PROGRESS;

        Task task = new Task(name, description);
        task.setStatus(status);
        Task taskToCompare = new Task(name, description);
        taskToCompare.setStatus(status);

        assertEquals(task, taskToCompare, "Две одинаковые задачи должны быть эквивалентны");
    }

    @Test
    public void checkTasksEqualsById() {
        Task task = new Task(1, "Name1");
        Task taskToCompare = new Task(1, "Name2");

        assertEquals(task, taskToCompare, "Две задачи с одинаковым id должны быть эквивалентны");
    }

}