package com.taskmanager.service;

import com.taskmanager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void prepare() {
        this.taskManager = new InMemoryTaskManager();
    }

    @Test
    public void checkAddTaskNewTask() {
        Task expectedTask = new Task("testTask", "description");
        taskManager.addTask(expectedTask);

        assertEquals(1, taskManager.getTasks().size(), "Ожидался список из 1 элемента");
        assertTrue(taskManager.getTasks().contains(expectedTask), "Не обнаружен добавленный элемент");

    }

    @Test
    public void checkAddTaskAlreadyAddedTask() {
        Task task = new Task("testTask", "description");
        taskManager.addTask(task);
        taskManager.addTask(new Task(task));

        assertEquals(1, taskManager.getTasks().size(), "Ожидался список из 1 элемента");
    }

    @Test
    public void checkAddEpicNewEpic() {
        Epic epic = new Epic("testEpic", "description");
        epic.addNewSubtask(2);
        taskManager.addEpic(epic);

        assertEquals(1, taskManager.getEpics().size(), "Ожидался список из 1 элемента");
        assertTrue(taskManager.getEpics().contains(epic), "Не обнаружен добавленный элемент");

    }

    @Test
    public void checkAddEpicAlreadyAddedEpic() {
        Epic epic = new Epic("testEpic", "description");
        epic.addNewSubtask(2);
        taskManager.addEpic(epic);
        taskManager.addEpic(new Epic(epic));

        assertEquals(1, taskManager.getEpics().size(), "Ожидался список из 1 элемента");
    }

    @Test
    public void checkAddSubtaskNewSubtask() {
        Epic epic = new Epic("testEpic", "description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", "descriptionSub", epic.getId());
        taskManager.addSubtask(subtask);

        // Проверка добавления в общий список подзадач
        assertEquals(1, taskManager.getSubtasks().size(), "Ожидался список из 1 элемента");
        assertTrue(taskManager.getSubtasks().contains(subtask), "Не обнаружен добавленный элемент");

        // Проверка добавления в список подзадач эпика
        List<Integer> subtaskList = taskManager.getEpicById(epic.getId()).getSubtaskList();
        assertEquals(1, subtaskList.size(), "Ожидался список из 1 элемента");
        assertTrue(subtaskList.contains(subtask.getId()), "Не обнаружен добавленный элемент");

    }

    @Test
    public void checkAddSubtaskAlreadyAddedSubtask() {
        Epic epic = new Epic("testEpic", "description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", "descriptionSub", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(new Subtask(subtask));

        assertEquals(1, taskManager.getSubtasks().size(), "Ожидался список из 1 элемента");
    }

    @Test
    public void checkEditTask() {
        Task task = new Task("oldName", "oldDescription");
        taskManager.addTask(task);
        task.setName("newName");
        task.setDescription("newDescription");
        taskManager.editTask(task);

        assertEquals(1, taskManager.getTasks().size(), "Ожидался список из 1 элемента");
        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не изменилась");
    }

    @Test
    public void checkEditEpic() {
        Epic epic = new Epic("oldName", "oldDescription");
        epic.addNewSubtask(10);
        taskManager.addEpic(epic);
        epic.setName("newName");
        epic.setDescription("newDescription");
        epic.addNewSubtask(11);
        taskManager.editEpic(epic);

        assertEquals(1, taskManager.getEpics().size(), "Ожидался список из 1 элемента");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпик не изменился");
    }

    @Test
    public void checkEditSubtask() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("oldName", "oldDescription", epic.getId());
        taskManager.addSubtask(subtask);
        subtask.setName("newName");
        subtask.setDescription("newDescription");
        taskManager.editSubtask(subtask);

        assertEquals(1, taskManager.getSubtasks().size(), "Ожидался список из 1 элемента");
        assertEquals(1, taskManager.getEpicById(epic.getId()).getSubtaskList().size(), "Ожидался список из 1 элемента");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадача не изменилось");
    }

    @Test
    public void getTasks() {
        int taskCount = 5;
        for (int i = 0; i < taskCount; i++) {
            taskManager.addTask(new Task("task" + i));
        }

        assertEquals(taskCount, taskManager.getTasks().size(), "Ожидался список из " + taskCount + " элементов");
    }

    @Test
    public void getEpics() {
        int epicCount = 5;
        for (int i = 0; i < epicCount; i++) {
            taskManager.addEpic(new Epic("epic" + i));
        }

        assertEquals(epicCount, taskManager.getEpics().size(), "Ожидался список из " + epicCount + " элементов");
    }

    @Test
    public void getSubtasks() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            taskManager.addSubtask(new Subtask("subtask" + i, epic.getId()));
        }

        assertEquals(subtaskCount, taskManager.getSubtasks().size(), "Ожидался список из " + subtaskCount + " элементов");
    }

    @Test
    public void checkGetAllEntities() {
        Task task = new Task("testTask");
        taskManager.addTask(task);
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        List<AbstractTask> allEntities = taskManager.getAllEntities();

        assertEquals(3, allEntities.size(), "Ожидалось 3 сущности");
        assertTrue(allEntities.contains(task), "Отсутствует добавленная задача в общей выгрузке всех сущностей из таск-менеджера");
        assertTrue(allEntities.contains(epic), "Отсутствует добавленный эпик в общей выгрузке всех сущностей из таск-менеджера");
        assertTrue(allEntities.contains(subtask), "Отсутствует добавленная подзадача в общей выгрузке всех сущностей из таск-менеджера");
    }

    @Test
    public void checkGetSubtaskListByEpicId() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        List<Subtask> subtaskList = taskManager.getSubtaskListByEpicId(epic.getId());

        assertEquals(1, subtaskList.size(), "Должна была вернуться только 1 подзадача в эпике");
        assertTrue(subtaskList.contains(subtask), "Должна была вернуться добавленная в эпик подзадача");
    }

    @Test
    public void checkDeleteAllTasks() {
        int taskCount = 5;
        for (int i = 0; i < taskCount; i++) {
            taskManager.addTask(new Task("task" + i));
        }

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getTasks().size(), "Ожидался пустой список задач");

    }

    @Test
    public void checkDeleteAllEpics() {
        int epicCount = 5;
        for (int i = 0; i < epicCount; i++) {
            Epic tempEpic = new Epic("epic" + i);
            taskManager.addEpic(tempEpic);
            taskManager.addSubtask(new Subtask("subtask", tempEpic.getId()));
        }

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getEpics().size(), "Ожидался пустой список эпиков");
        assertEquals(0, taskManager.getSubtasks().size(), "Ожидался пустой список подзадач");
    }

    @Test
    public void checkDeleteAllSubtasks() {
        List<Epic> epics = new ArrayList<>();
        int epicCount = 5;
        for (int i = 0; i < epicCount; i++) {
            Epic tempEpic = new Epic("epic" + i);
            epics.add(tempEpic);
            taskManager.addEpic(tempEpic);
            taskManager.addSubtask(new Subtask("subtask", tempEpic.getId()));
        }

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size(), "Ожидался пустой список подзадач");
        for (Epic epic : epics) {
            assertEquals(0, taskManager.getSubtaskListByEpicId(epic.getId()).size(), "Ожидалось, что списки подзадач у эпиков также очистятся");
        }
    }

    @Test
    public void checkDeleteAllSubtasksInEpic() {
        Epic epic = new Epic("testEpic");
        Epic epicForDel = new Epic("testEpicForDel");
        taskManager.addEpic(epic);
        taskManager.addEpic(epicForDel);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            taskManager.addSubtask(new Subtask("subtask" + i, epic.getId()));
            taskManager.addSubtask(new Subtask("subtaskForDel" + i, epicForDel.getId()));
        }

        taskManager.deleteAllSubtasksInEpic(epicForDel.getId());

        assertEquals(subtaskCount, taskManager.getSubtasks().size(), "Некорректная длина списка всех подзадач");
        assertEquals(0, taskManager.getEpicById(epicForDel.getId()).getSubtaskList().size(), "Ожидался пустой список подзадач");

    }

    @Test
    public void checkGetTaskById() {
        Task task = new Task("testTask");
        taskManager.addTask(task);

        Task taskReceived = taskManager.getTaskById(task.getId());

        assertEquals(task, taskReceived, "Возвращена другая задача");
    }

    @Test
    public void checkGetEpicById() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);

        Epic epicReceived = taskManager.getEpicById(epic.getId());

        assertEquals(epic, epicReceived, "Возвращён другой эпик");
    }

    @Test
    public void checkGetSubtaskById() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        Subtask subtaskReceived = taskManager.getSubtaskById(subtask.getId());

        assertEquals(subtask, subtaskReceived, "Возвращена другая подзадача");
    }

    @Test
    public void checkDeleteTaskById() {
        Task task = new Task("testTask");
        taskManager.addTask(task);

        taskManager.deleteTaskById(task.getId());

        assertEquals(0, taskManager.getTasks().size(), "Ожидался пустой список задач");
    }

    @Test
    public void checkDeleteEpicById() {
        Epic epic = new Epic("testEpic");
        Epic epicForDel = new Epic("testEpicForDel");
        taskManager.addEpic(epic);
        taskManager.addEpic(epicForDel);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.deleteEpicById(epicForDel.getId());

        List<Integer> epicIds = taskManager.getSubtaskListByEpicId(epic.getId())
                .stream().map(Subtask::getEpicId).toList();

        assertEquals(1, taskManager.getEpics().size(), "Ожидался список из 1 эпика");
        assertFalse(epicIds.contains(epicForDel.getId()), "Найден номер удалённого эпика");
    }

    @Test
    public void checkDeleteSubtaskById() {
        Epic epic = new Epic("testEpic1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        Subtask subtaskForDel = new Subtask("testSubtaskForDel", epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtaskForDel);

        taskManager.deleteSubtaskById(subtaskForDel.getId());

        List<Integer> subtasksIds = taskManager.getSubtaskListByEpicId(epic.getId())
                .stream().map(Subtask::getId).toList();

        assertEquals(1, taskManager.getSubtasks().size(), "Ожидался список из 1 подзадачи");
        assertFalse(subtasksIds.contains(subtaskForDel.getId()), "Найден номер удалённой подзадачи");
    }

    @Test
    public void checkGetNextId() {
        Task task = new Task("testTask");
        taskManager.addTask(task);
        assertEquals(1, task.getId(), "Ожидался номер 1");

        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        assertEquals(2, epic.getId(), "Ожидался номер 2");

        Subtask subtask = new Subtask("testSubtack", epic.getId());
        taskManager.addSubtask(subtask);
        assertEquals(3, subtask.getId(), "Ожидался номер 3");
    }

    @Test
    public void checkGetNextIdAfterDeletingTask() {
        Task taskForDel = new Task("testTaskForDel");
        taskManager.addTask(taskForDel);
        taskManager.deleteTaskById(taskForDel.getId());

        Task task = new Task("testTask");
        taskManager.addTask(task);
        assertEquals(2, task.getId(), "Ожидался номер 2");

    }

    @Test
    public void checkGetNextIdAfterDeletingEpic() {
        Epic epicForDel = new Epic("testEpicForDel");
        taskManager.addEpic(epicForDel);
        taskManager.deleteEpicById(epicForDel.getId());

        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);

        assertEquals(2, epic.getId(), "Ожидался номер 2");

    }

    @Test
    public void checkGetNextIdAfterDeletingSubtask() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtaskForDel = new Subtask("testSubtaskForDel", epic.getId());
        taskManager.addSubtask(subtaskForDel);
        taskManager.deleteSubtaskById(subtaskForDel.getId());

        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(3, subtask.getId(), "Ожидался номер 3");

    }

    @Test
    public void checkNewTaskStatus() {
        Task task = new Task("testTask");
        taskManager.addTask(task);

        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    public void checkNewEpicStatus() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkNewSubtaskStatus() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusOneSubtaskInProcess() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            taskManager.addSubtask(new Subtask("subtask" + i, epic.getId()));
        }

        Subtask firstSubtask = taskManager.getSubtasks().getFirst();
        firstSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.editSubtask(firstSubtask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusAllSubtasksInProcess() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            Subtask tempSubtask = new Subtask("subtask" + i, epic.getId());
            tempSubtask.setStatus(Status.IN_PROGRESS);
            taskManager.addSubtask(tempSubtask);
        }

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusOneSubtaskDone() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            taskManager.addSubtask(new Subtask("subtask" + i, epic.getId()));
        }

        Subtask firstSubtask = taskManager.getSubtasks().getFirst();
        firstSubtask.setStatus(Status.DONE);
        taskManager.editSubtask(firstSubtask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusAllSubtasksDone() {
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            Subtask tempSubtask = new Subtask("subtask" + i, epic.getId());
            tempSubtask.setStatus(Status.DONE);
            taskManager.addSubtask(tempSubtask);
        }

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusSetNewAfterDone() {

        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            Subtask tempSubtask = new Subtask("subtask" + i, epic.getId());
            tempSubtask.setStatus(Status.DONE);
            taskManager.addSubtask(tempSubtask);
        }
        taskManager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.NEW));
        taskManager.getSubtasks().forEach(subtask -> taskManager.editSubtask(subtask));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkCorrectEpicStatusSetNewAfterProgress() {

        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        int subtaskCount = 5;
        for (int i = 0; i < subtaskCount; i++) {
            Subtask tempSubtask = new Subtask("subtask" + i, epic.getId());
            taskManager.addSubtask(tempSubtask);
            tempSubtask.setStatus(Status.IN_PROGRESS);
        }
        taskManager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.NEW));
        taskManager.getSubtasks().forEach(subtask -> taskManager.editSubtask(subtask));
        taskManager.getSubtasks().forEach(subtask -> taskManager.editSubtask(subtask));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkGetHistory() {
        Task task = new Task("testTask");
        taskManager.addTask(task);
        Epic epic = new Epic("testEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("testSubtask", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<AbstractTask> history = taskManager.getHistory();

        assertEquals(3, history.size(), "Ожидалась список просмотренных задач из 3 элементов");
        assertTrue(history.contains(task), "Не найдена просмотренная задача в истории");
        assertTrue(history.contains(epic), "Не найден просмотренный эпик в истории");
        assertTrue(history.contains(subtask), "Не найдена просмотренная подзадача в истории");
    }


    @Test
    public void checkGetHistoryModifiedTasks() {
        String name = "name";
        Task task = new Task(name);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        String anotherName = "anotherName";
        Task taskModified = new Task(task);
        taskModified.setName(anotherName);
        taskManager.editTask(taskModified);
        taskManager.getTaskById(task.getId());

        assertTrue(taskManager.getHistory().containsAll(List.of(task, taskModified)), "В истории должны хранится старые версии задач");
    }
}