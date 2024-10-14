package com.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void prepare() {
        this.epic = new Epic(1, "testEpic");
        this.subtask = new Subtask(2, "testSubtask", epic.getId());
    }

    @Test
    public void addNewSubtask() {
        epic.addNewSubtask(subtask.getId());

        assertEquals(epic.getSubtaskList().size(), 1, "Ожидался список подзадач с 1 элементом");
        assertTrue(epic.getSubtaskList().contains(subtask.getId()), "Не найден добавленная в эпик подзадача");
    }

    @Test
    public void deleteSubtaskById() {
        epic.addNewSubtask(subtask.getId());
        epic.deleteSubtaskById(subtask.getId());

        assertEquals(epic.getSubtaskList().size(), 0, "Ожидался пустой список подзадач");
    }

    @Test
    public void deleteAllSubtasks() {
        Subtask subtaskOneMore = new Subtask(3, "oneMore", epic.getId());
        epic.addNewSubtask(subtask.getId());
        epic.addNewSubtask(subtaskOneMore.getId());
        epic.deleteAllSubtasks();

        assertEquals(epic.getSubtaskList().size(), 0, "Ожидался пустой список подзадач");
    }

    @Test
    public void checkEpicsEqualsByAllFields() {
        String name = "name";
        String description = "description";
        Status status = Status.IN_PROGRESS;

        Epic epic = new Epic(name, description);
        epic.setStatus(status);
        epic.addNewSubtask(1);
        Epic epicToCompare = new Epic(name, description);
        epicToCompare.setStatus(status);
        epicToCompare.addNewSubtask(1);

        assertEquals(epic, epicToCompare, "Два одинаковых эпика должны быть эквивалентны");
    }

    @Test
    public void checkEpicsEqualsById() {
        Epic epic = new Epic(1, "Name1");
        Epic epicToCompare = new Epic(1, "Name2");

        assertEquals(epic, epicToCompare, "Два эпика с одинаковым id должны быть эквивалентны");
    }
}