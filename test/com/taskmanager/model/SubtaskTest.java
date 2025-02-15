package com.taskmanager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void checkSubtasksEqualsByAllFields() {
        String name = "name";
        String description = "description";
        Status status = Status.IN_PROGRESS;
        int epicId = 1;

        Subtask subtask = new Subtask(name, description, epicId);
        subtask.setStatus(status);
        subtask.setEpicId(epicId);
        Subtask subtaskToCompare = new Subtask(name, description, epicId);
        subtaskToCompare.setStatus(status);
        subtask.setEpicId(epicId);

        assertEquals(subtask, subtaskToCompare, "Два одинаковых эпика должны быть эквивалентны");
    }

    @Test
    public void checkSubtasksEqualsById() {
        Subtask subtask = new Subtask(1, "Name1", 1);
        Subtask subtaskToCompare = new Subtask(1, "Name2", 1);

        assertEquals(subtask, subtaskToCompare, "Два эпика с одинаковым id должны быть эквивалентны");
    }

}