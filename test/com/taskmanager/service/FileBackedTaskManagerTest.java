package com.taskmanager.service;

import com.taskmanager.model.Epic;
import com.taskmanager.model.Subtask;
import com.taskmanager.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File file;
    private final Task task = new Task(1, "Задача", "Описание залачи");
    private final Epic epic = new Epic(2, "Эпик", "Описание эпика");
    private final Subtask subtask = new Subtask(3, "Подзадача", "Описание подзадачи", 2);

    @BeforeEach
    public void prepare() throws IOException {
        file = Files.createTempFile(Paths.get("/Users/nirta/IdeaProjects/java-dev/javadev_sprint_4/resources"),
                "task", ".csv").toFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write("id,type,name,status,description,epic\n");
        writer.write(task + "\n");
        writer.write(epic + "\n");
        writer.write(subtask + "\n");
        writer.close();
    }

    @AfterEach
    public void cleanUp() {
        file.deleteOnExit();
    }

    @Test
    public void checkReadFromFileAnyTypes() {
        TaskManager taskManager = Managers.loadFromFile(file);

        assertTrue(taskManager.getTasks().contains(task), "Задача не была добавлена из файла в таск-менеджер");
        assertTrue(taskManager.getEpics().contains(epic), "Эпик не был добавлен из файла в таск-менеджер");
        assertTrue(taskManager.getSubtasks().contains(subtask), "Подзадача не была добавлена из файла в таск-менеджер");
    }

    @Test
    public void checkReadFromFileWrongType() throws IOException {
        FileWriter writer = new FileWriter(file, true);
        writer.append("4,TYPE,Неправильного типа задача,NEW,,");
        writer.close();

        TaskManager taskManager = null;
        try {
            taskManager = Managers.loadFromFile(file);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        assertNull(taskManager, "Таск-менеджер не должен заполняться");
    }

    @Test
    public void checkSaveTask() throws IOException {
        Task tempTask = new Task(4, "Проверочная задача");

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.addTask(tempTask);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getTasks().contains(tempTask), "Задача не была добавлена из файла в таск-менеджер");
        assertTrue(text.contains(tempTask.toString()), "Задача не была добавлена в файл");
    }

    @Test
    public void checkSaveEpic() throws IOException {
        Epic tempEpic = new Epic(5, "Проверочный эпик");

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.addEpic(tempEpic);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getEpics().contains(tempEpic), "Эпик не был добавлен из файла в таск-менеджер");
        assertTrue(text.contains(tempEpic.toString()), "Задача не была добавлена в файл после изменения");
    }

    @Test
    public void checkSaveSubtask() throws IOException {
        Subtask tempSubtask = new Subtask(6, "Проверочная подзадача", 2);

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.addSubtask(tempSubtask);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getSubtasks().contains(tempSubtask), "Подзадача не была добавлена из файла в таск-менеджер");
        assertTrue(text.contains(tempSubtask.toString()), "Задача не была добавлена в файл после изменения");
    }

    @Test
    public void checkEditTask() throws IOException {
        Task tempTask = new Task(1, "Проверочная задача");

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.editTask(tempTask);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getTasks().contains(tempTask), "Задача не была изменена в таск-менеджере");
        assertTrue(text.contains(tempTask.toString()), "Задача не была добавлена в файл после изменения");
    }

    @Test
    public void checkEditEpic() throws IOException {
        Epic tempEpic = new Epic(2, "Изменения");

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.editEpic(tempEpic);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getEpics().contains(tempEpic), "Эпик не был изменен в таск-менеджере");
        assertTrue(text.contains(tempEpic.toString()), "Эпик не был добавлен в файл после изменения");
    }

    @Test
    public void checkEditSubtask() throws IOException {
        Subtask tempSubtask = new Subtask(3, "Изменения", 2);

        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.editSubtask(tempSubtask);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertTrue(taskManager.getSubtasks().contains(tempSubtask), "Подзадача не была изменена в таск-менеджере");
        assertTrue(text.contains(tempSubtask.toString()), "Подзадача не была добавлена в файл после изменения");
    }

    @Test
    public void checkDeleteTask() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteTaskById(1);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getTasks().contains(task), "Задача не была удалена из таск-менеджера");
        assertFalse(text.contains(task.toString()), "Задача не была удалена из файла");
    }

    @Test
    public void checkDeleteEpic() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteEpicById(2);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getEpics().contains(epic), "Эпик не был удален из таск-менеджера");
        assertFalse(text.contains(epic.toString()), "Эпик не был удален из файла");
    }

    @Test
    public void checkDeleteSubtask() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteSubtaskById(3);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не была удалена из таск-менеджера");
        assertFalse(text.contains(subtask.toString()), "Подзадача не была удалена из файла");
    }

    @Test
    public void checkDeleteAllTasks() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteAllTasks();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getTasks().contains(task), "Задача не была удалена из таск-менеджера");
        assertFalse(text.contains(task.toString()), "Задача не была удалена из файла");
    }

    @Test
    public void checkDeleteAllEpics() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteAllEpics();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getEpics().contains(epic), "Эпик не был удален из таск-менеджера");
        assertFalse(text.contains(epic.toString()), "Эпик не был удален из файла");
    }

    @Test
    public void checkDeleteAllSubtasks() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteAllSubtasks();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не была удалена из таск-менеджера");
        assertFalse(text.contains(subtask.toString()), "Подзадача не была удалена из файла");
    }

    @Test
    public void checkDeleteAllSubtasksInEpic() throws IOException {
        TaskManager taskManager = Managers.loadFromFile(file);
        taskManager.deleteAllSubtasksInEpic(2);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> text = reader.lines().toList();

        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не была удалена из таск-менеджера");
        assertEquals(0, taskManager.getEpicById(2).getSubtaskList().size(), "В эпике остались подзадачи");
        assertFalse(text.contains(subtask.toString()), "Подзадача не была удалена из файла");
    }

}
