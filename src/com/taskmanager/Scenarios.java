package com.taskmanager;

import com.taskmanager.model.*;
import com.taskmanager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Scenarios {

    private List<LocalDateTime> dateTimes = List.of(
            LocalDateTime.of(LocalDate.of(2025, 1, 1), LocalTime.of(13, 10, 25)),
            LocalDateTime.of(LocalDate.of(2025, 1, 1), LocalTime.of(16, 30, 30)),
            LocalDateTime.of(LocalDate.of(2025, 1, 2), LocalTime.of(13, 10, 25)),
            LocalDateTime.of(LocalDate.of(2025, 1, 3), LocalTime.of(13, 10, 25)),
            LocalDateTime.of(LocalDate.of(2025, 1, 4), LocalTime.of(13, 10, 25)),
            LocalDateTime.of(LocalDate.of(2025, 1, 5), LocalTime.of(9, 15, 0)),
            LocalDateTime.of(LocalDate.of(2025, 1, 6), LocalTime.of(9, 15, 0)),
            LocalDateTime.of(LocalDate.of(2025, 1, 7), LocalTime.of(9, 15, 0)),
            LocalDateTime.of(LocalDate.of(2025, 1, 8), LocalTime.of(9, 15, 0)),
            LocalDateTime.of(LocalDate.of(2025, 1, 9), LocalTime.of(9, 15, 0)),
            LocalDateTime.of(LocalDate.of(2025, 1, 10), LocalTime.of(20, 30, 40)),
            LocalDateTime.of(LocalDate.of(2025, 1, 11), LocalTime.of(20, 30, 40)),
            LocalDateTime.of(LocalDate.of(2025, 1, 12), LocalTime.of(20, 30, 40)),
            LocalDateTime.of(LocalDate.of(2025, 1, 13), LocalTime.of(20, 30, 40)),
            LocalDateTime.of(LocalDate.of(2025, 1, 14), LocalTime.of(20, 30, 40))
    );

    private List<Duration> durations = List.of(
            Duration.ofMinutes(30),
            Duration.ofMinutes(60),
            Duration.ofMinutes(90)
    );

    private final List<Task> testTaskList = List.of(
            new Task(1, "Ответить на письма", dateTimes.get(0), durations.get(0)),
            new Task(2, "Отправить посылку по почте", "Адрес получателя в личном кабинете", dateTimes.get(1), durations.get(1)),
            new Task(17, "Получить посылку", dateTimes.get(0), durations.get(1))
    );

    private final List<Epic> testEpicList = List.of(
            new Epic(3, "Сделать уборку"),
            new Epic(4, "Пройти спринт на Практикуме"),
            new Epic(5, "Пройти спринт 9 на Практикуме", "Пройти его уже наконец-то"),
            new Epic(6, "Глобальная задача без плана", "Нет плана - нет пунктов")
    );

    private final List<Subtask> testSubtaskList = List.of(
            new Subtask(7, "Вытереть пыль", "Для мониторов не забыть использовать специальную жидкость", testEpicList.get(0).getId(), dateTimes.get(2), durations.get(0)),
            new Subtask(8, "Помыть полы", testEpicList.get(0).getId(), dateTimes.get(3), durations.get(1)),
            new Subtask(9, "Вымыть сантехнику", testEpicList.get(0).getId(), dateTimes.get(8), durations.get(2)),
            new Subtask(10, "Вынести мусор", testEpicList.get(0).getId(), dateTimes.get(7), durations.get(0)),
            new Subtask(11, "Пройти теорию", testEpicList.get(1).getId(), dateTimes.get(6), durations.get(1)),
            new Subtask(12, "Посетить вебинар", testEpicList.get(1).getId(), dateTimes.get(5), durations.get(2)),
            new Subtask(13, "Выполнить практику", testEpicList.get(1).getId(), dateTimes.get(4), durations.get(0)),
            new Subtask(14, "Написать список", testEpicList.get(2).getId(), dateTimes.get(9), durations.get(1)),
            new Subtask(15, "Слинковать с мапой", testEpicList.get(2).getId(), dateTimes.get(9), durations.get(2)),
            new Subtask(16, "Исправить другие методы", testEpicList.get(2).getId(), dateTimes.get(10), durations.get(0))
    );

    public void testAdd(TaskManager taskManager) {
        System.out.println("1. Добавление задач, эпиков и подзадач.");
        for (Task task : testTaskList) {
            taskManager.addTask(task);
        }

        for (Epic epic : testEpicList) {
            taskManager.addEpic(epic);
        }

        for (Subtask subtask : testSubtaskList) {
            taskManager.addSubtask(subtask);
        }

        System.out.println("Добавлено: " + taskManager.getTasks().size() + " задач, " + taskManager.getEpics().size() + " эпиков, " + taskManager.getSubtasks().size() + " подзадач\n");
    }

    public void testEdit(TaskManager taskManager) {
        System.out.println("2. Редактирование задач, эпиков и подзадач.");
        taskManager.editTask(new Task(testTaskList.get(1).getId(), "Отправить посылку через СДЭК"));
        taskManager.editEpic(new Epic(testEpicList.get(1).getId(), "Пройти спринт на Практикуме", "5 спринт"));
        taskManager.editSubtask(new Subtask(testSubtaskList.get(3).getId(), "Вынести мусор из всех комнат", "Кабинет кухня ванная", testSubtaskList.get(3).getEpicId(), dateTimes.get(12), durations.get(0)));
        System.out.println();
    }

    public void testGetTasks(TaskManager taskManager) {
        System.out.println("Перечень всех задач в таск-менеджере:");
        System.out.println("Задачи: " + taskManager.getTasks().toString());
        System.out.println("Эпики: " + taskManager.getEpics().toString());
        System.out.println("Подзадачи: " + taskManager.getSubtasks().toString());
        System.out.println();
    }

    public void testGetPrioritizedTasks(TaskManager taskManager) {
        System.out.println("Приоритизированный перечень задач и подзадач:");
        System.out.println(taskManager.getPrioritizedTasks().toString());
        System.out.println();
    }

    public void updateStatuses(TaskManager taskManager) {
        System.out.println("3. Обновление статусов задач, эпиков и подзадач.");

        System.out.println("3.1. Задача");
        System.out.println(testTaskList.get(1) + "\n");

        System.out.println(Status.NEW + " -> " + Status.IN_PROGRESS);
        Task taskToProgress = new Task(testTaskList.get(1));
        taskToProgress.setStatus(Status.IN_PROGRESS);
        taskManager.editTask(taskToProgress);
        System.out.println("Текущий статус таски: " + taskManager.getTaskById(2).getStatus().toString());

        System.out.println(Status.IN_PROGRESS + " -> " + Status.DONE);
        Task taskToDone = new Task(testTaskList.get(1));
        taskToDone.setStatus(Status.DONE);
        taskManager.editTask(taskToDone);
        System.out.println("Текущий статус таски: " + taskManager.getTaskById(2).getStatus().toString());

        System.out.println();

        System.out.println("3.2. Эпики + подзадачи");
        System.out.println("Эпик: " + testEpicList.get(1));
        System.out.println("Подзадачи: " + testSubtaskList.get(4) + "\n" + testSubtaskList.get(5) + "\n" + testSubtaskList.get(6) + "\n");

        System.out.println(Status.NEW + " -> " + Status.IN_PROGRESS);
        updateSubtasksToCustomStatus(taskManager, Status.IN_PROGRESS, testSubtaskList.get(4), testSubtaskList.get(5), testSubtaskList.get(6));
        System.out.println("Текущий статус эпика: " + taskManager.getEpicById(4).getStatus().toString() + "\n");

        System.out.println(Status.IN_PROGRESS + " -> " + Status.DONE);
        updateSubtasksToCustomStatus(taskManager, Status.DONE, testSubtaskList.get(4), testSubtaskList.get(5), testSubtaskList.get(6));
        System.out.println("Текущий статус эпика: " + taskManager.getEpicById(4).getStatus().toString() + "\n");

        System.out.println(Status.DONE + " -> " + Status.IN_PROGRESS);
        updateSubtasksToCustomStatus(taskManager, Status.NEW, testSubtaskList.get(4));
        System.out.println("Текущий статус эпика: " + taskManager.getEpicById(4).getStatus().toString());

        System.out.println();
    }

    private void updateSubtasksToCustomStatus(TaskManager taskManager, Status status, Subtask... subtaskList) {
        for (Subtask subtask : subtaskList) {
            Subtask tempSubtask = new Subtask(subtask);
            tempSubtask.setStatus(status);
            taskManager.editSubtask(tempSubtask);

            System.out.println("Для подзадачи с номером " + subtask.getId() + " из эпика " + subtask.getEpicId() +
                    " установлен статус " + status);
        }
    }

    public static void testHistory(TaskManager taskManager) {
        System.out.println("4. Просмотр истории");

        System.out.println(taskManager.getHistory());
        System.out.println("Количество элементов в истории: " + taskManager.getHistory().size());

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(11);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(9);
        taskManager.getSubtaskById(8);
        taskManager.getEpicById(5);
        taskManager.getSubtaskById(14);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(11);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(13);
        taskManager.getTaskById(2);
        taskManager.getEpicById(5);
        taskManager.getSubtaskById(15);
        taskManager.getSubtaskById(12);
        taskManager.getSubtaskById(14);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(16);

        // 4 1 9 8 11 7 13 2 5 15 12 14 3 16
        System.out.println(taskManager.getHistory());
        System.out.println("Количество элементов в истории: " + taskManager.getHistory().size());

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(5);

        // 4 1 9 8 11 7 13 12 3
        System.out.println(taskManager.getHistory());
        System.out.println("Количество элементов в истории: " + taskManager.getHistory().size());

    }

    public void testDelete(TaskManager taskManager) {
        System.out.println("5. Удаление задач, эпиков и подзадач.");

        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(10);
        taskManager.deleteEpicById(4);
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

}
