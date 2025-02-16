package com.taskmanager.service.managers;

import com.taskmanager.model.*;
import com.taskmanager.service.exceptions.AlreadyExistsException;
import com.taskmanager.service.exceptions.NotFoundException;
import com.taskmanager.service.exceptions.TimeOverlapException;
import com.taskmanager.service.exceptions.WithouIdException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс таск-менеджера, реализующий хранение задач в текущей памяти
 */
public class InMemoryTaskManager implements TaskManager {
    /**
     * Список задач
     */
    private final Map<Integer, Task> tasks;
    /**
     * Список эпиков
     */
    private final Map<Integer, Epic> epics;
    /**
     * Список подзадач
     */
    private final Map<Integer, Subtask> subtasks;

    private final TreeSet<AbstractTask> prioritizedTasks;
    /**
     * Менеджер для работы с историей просмотра
     */
    private final HistoryManager historyManager;

    /**
     * Текущий свободный идентификатор
     */
    private int currentId;

    /**
     * Конструктор для создания нового таск-менеджера
     */
    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(AbstractTask::getStartTime));
        this.historyManager = Managers.getDefaultHistory();
        this.currentId = 1;
    }

    @Override
    public void addTask(Task task) {
        if (tasks.containsValue(task)) {
            throw new AlreadyExistsException("Такая задача уже была добавлена");
        }

        if (isTasksOverlap(task)) {
            throw new TimeOverlapException("Задача пересекается по времени с уже добавленными задачами");
        }

        if (task.getId() != null) {
            updateCurrentId(task.getId());
        } else {
            task.setId(getNextId());
        }
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

    }

    @Override
    public void addEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            throw new AlreadyExistsException("Такой эпик уже был добавлен");
        }

        if (epic.getId() != null) {
            updateCurrentId(epic.getId());
        } else {
            epic.setId(getNextId());
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {
            if (Objects.equals(subtasks.values().stream().filter(item -> item.equals(subtask)).findFirst().orElseThrow().getEpicId(), subtask.getEpicId())) {
                throw new AlreadyExistsException("Такая подзадача в эпик с id = " + subtask.getEpicId() + " уже была добавлена");
            }
        }

        if (isTasksOverlap(subtask)) {
            throw new TimeOverlapException("Подзадача пересекается по времени с уже добавленными задачами");
        }

        if (subtask.getId() != null) {
            updateCurrentId(subtask.getId());
        } else {
            subtask.setId(getNextId());
        }
        subtasks.put(subtask.getId(), subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        epics.get(subtask.getEpicId()).addNewSubtask(subtask.getId());
        correctEpicStatus(subtask.getEpicId());
        correctEpicDuration(epics.get(subtask.getEpicId()));
    }

    @Override
    public void editTask(Task task) {
        if (task.getId() == null) {
            throw new WithouIdException("Передана задача без id. Невозможно обновить");
        }
        boolean inTaskInPrioritizedList = prioritizedTasks.stream().map(AbstractTask::getId).anyMatch(id -> task.getId().equals(id));

        if (!inTaskInPrioritizedList && isTasksOverlap(task)) {
            throw new TimeOverlapException("Задача пересекается по времени с уже добавленными задачами");
        }

        if (inTaskInPrioritizedList) {
            final Task priTask = (Task) prioritizedTasks.stream().filter(t -> t.getId().equals(task.getId())).findFirst().orElseThrow();
            if (!priTask.getStartTime().equals(task.getStartTime())
                    || !priTask.getDuration().equals(task.getDuration())) {
                prioritizedTasks.remove(priTask);

                if (isTasksOverlap(task)) {
                    prioritizedTasks.add(priTask);
                    throw new TimeOverlapException("Задача пересекается по времени с уже добавленными задачами");
                }
                prioritizedTasks.add(priTask);
            }
        }

        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        } else {
            Optional<AbstractTask> taskToDelete = prioritizedTasks.stream().filter(t -> t.getId().equals(task.getId())).findFirst();
            taskToDelete.ifPresent(prioritizedTasks::remove);
        }
    }

    @Override
    public void editEpic(Epic epic) {
        if (epic.getId() == null) {
            throw new WithouIdException("Передан эпик без id. Невозможно обновить");
//            System.out.println("Передан эпик без id. Невозможно обновить");
//            return;
        }

        final Epic epicSaved = epics.get(epic.getId());
        epicSaved.setName(epic.getName());
        epicSaved.setDescription(epic.getDescription());
    }

    @Override
    public void editSubtask(Subtask subtask) {
        if (subtask.getId() == null || subtask.getEpicId() == null) {
            throw new WithouIdException("Передана подзадача без id или без привязки к эпику. Невозможно обновить");
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new NotFoundException("Передана подзадача с привязкой к несуществующему эпику. Невозможно обновить");
        }

        boolean isSubtaskInPrioritizedList = prioritizedTasks.stream().map(AbstractTask::getId).anyMatch(id -> subtask.getId().equals(id));

        if (!isSubtaskInPrioritizedList && isTasksOverlap(subtask)) {
            throw new TimeOverlapException("Задача пересекается по времени с уже добавленными задачами");
        }

        if (isSubtaskInPrioritizedList) {
            final Subtask priSubtask = (Subtask) prioritizedTasks.stream().filter(task -> task.getId().equals(subtask.getId())).findFirst().orElseThrow();
            if (!priSubtask.getStartTime().equals(subtask.getStartTime())
                    || !priSubtask.getDuration().equals(subtask.getDuration())) {
                prioritizedTasks.remove(priSubtask);

                if (isTasksOverlap(subtask)) {
                    prioritizedTasks.add(priSubtask);
                    throw new TimeOverlapException("Задача пересекается по времени с уже добавленными задачами");
                }
                prioritizedTasks.add(priSubtask);
            }
        }

        subtasks.put(subtask.getId(), subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        } else {
            Optional<AbstractTask> subtaskToDelete = prioritizedTasks.stream().filter(t -> t.getId().equals(subtask.getId())).findFirst();
            subtaskToDelete.ifPresent(prioritizedTasks::remove);
        }

        correctEpicStatus(subtask.getEpicId());
        correctEpicDuration(epics.get(subtask.getEpicId()));
    }

    @Override
    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return List.copyOf(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public List<AbstractTask> getAllEntities() {
        List<AbstractTask> allEntities = new ArrayList<>();

        allEntities.addAll(tasks.values());
        allEntities.addAll(epics.values());
        allEntities.addAll(subtasks.values());

        return allEntities;
    }

    @Override
    public List<Subtask> getSubtaskListByEpicId(Integer epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Не найден эпик с id = " + epicId);
        }

        return epic.getSubtaskList().stream().map(this::getSubtaskById).toList();
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });

        subtasks.clear();
        epics.clear();

    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });

        epics.forEach((id, epic) -> {
            epic.deleteAllSubtasks();
            correctEpicStatus(epic.getId());
            correctEpicDuration(epic);
        });

        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksInEpic(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Не найден эпик с id = " + epicId);
        }

        epic.getSubtaskList().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
        });

        epic.deleteAllSubtasks();

        correctEpicStatus(epicId);
        correctEpicDuration(epic);
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);

        if (task == null) {
            throw new NotFoundException("Не найдена задача с id = " + id);
        }

        historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);

        if (epic == null) {
            throw new NotFoundException("Не найден эпик с id = " + id);
        }

        historyManager.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            throw new NotFoundException("Не найдена подзадача с id = " + id);
        }

        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        final Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Не найдена задача с id = " + id);
        }
        prioritizedTasks.remove(task);

        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Не найден эпик с id = " + id);
        }

        historyManager.remove(id);

        epic.getSubtaskList().forEach(subtaskId -> {
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtaskId);
        });

    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Не найдена подзадача с id = " + id);
        }

        prioritizedTasks.remove(subtask);

        final int epicId = subtask.getEpicId();
        epics.get(epicId).deleteSubtaskById(id);
        correctEpicStatus(epicId);
        correctEpicDuration(epics.get(subtask.getEpicId()));

        historyManager.remove(id);
    }

    @Override
    public int getNextId() {
        return currentId++;
    }

    private void updateCurrentId(Integer newValue) {
        if (newValue > currentId) currentId = newValue + 1;
    }

    @Override
    public void correctEpicStatus(int epicId) {
        int countToDo = 0;
        int countDone = 0;
        final Epic epic = epics.get(epicId);
        final List<Integer> currentSubtaskList = epic.getSubtaskList();

        for (Integer id : currentSubtaskList) {
            switch (subtasks.get(id).getStatus()) {
                case NEW -> countToDo++;
                case DONE -> countDone++;
                default -> {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }

        int subtaskListSize = currentSubtaskList.size();
        if (countToDo == subtaskListSize) {
            epic.setStatus(Status.NEW);
        } else if (countDone == subtaskListSize) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<AbstractTask> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return List.copyOf(historyManager.getHistory());
    }

    private void correctEpicDuration(Epic epic) {
        final List<Subtask> subtaskList = epics.get(epic.getId()).getSubtaskList().stream().map(subtasks::get).toList();

        final List<Subtask> subtaskWithTime = subtaskList.stream().filter(subtask -> subtask.getStartTime() != null && !subtask.getStatus().equals(Status.DONE)).toList();
        if (subtaskList.isEmpty() || subtaskWithTime.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        // set start
        LocalDateTime startTime = subtaskWithTime.stream().map(AbstractTask::getStartTime).min(Comparator.naturalOrder()).orElseThrow();

        epic.setStartTime(startTime);

        // set end
        LocalDateTime endTime = subtaskWithTime.stream().map(AbstractTask::getEndTime).max(Comparator.naturalOrder()).orElseThrow();

        epic.setEndTime(endTime);

        // set duration
        Duration duration = Duration.between(startTime, endTime);
        epic.setDuration(duration);
    }

    private boolean isTasksOverlap(AbstractTask abstractTask) {
        if (abstractTask.getStartTime() == null) {
            return false;
        }

        final List<AbstractTask> overlap = prioritizedTasks.stream()
                .filter(task -> !task.getStartTime().isAfter(abstractTask.getStartTime().plus(abstractTask.getDuration()))
                        && !task.getStartTime().equals(abstractTask.getStartTime().plus(abstractTask.getDuration())))
                .filter(task -> !task.getStartTime().plus(task.getDuration()).isBefore(abstractTask.getStartTime())
                        && !task.getStartTime().plus(task.getDuration()).equals(abstractTask.getStartTime()))
                .toList();
        return !overlap.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return Objects.equals(tasks, that.tasks) && Objects.equals(epics, that.epics) && Objects.equals(subtasks, that.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epics, subtasks);
    }

    /**
     * Представление объекта таск-менеджера в виде строки
     *
     * @return Строка, описывающая объект таск-менеджера
     */
    @Override
    public String toString() {
        return InMemoryTaskManager.class.getName() + " { " + "currentId = " + currentId + ", " + "taskList = " + tasks + ", " + "epicList = " + epics + ", " + "subtaskList = " + subtasks + " }";
    }
}
