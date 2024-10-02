import java.util.*;

/**
 * Класс, обеспечивающий работу таск-менеджер с задачами
 */
public class TaskManager {
    /**
     * Текущий свободный идентификтор
     */
    private int currentId;
    /**
     * Список задач
     */
    private final HashMap<Integer, Task> taskList;
    /**
     * Список эпиков
     */
    private final HashMap<Integer, Epic> epicList;
    /**
     * Список подзадач
     */
    private final HashMap<Integer, Subtask> subtaskList;

    /**
     * Конструктор для создания нового таск-менеджера
     */
    public TaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
        this.currentId = 1;
    }

    /**
     * Получение номера (идентификатора) для задачи
     *
     * @return Идентификтор
     */
    private int getNextId() {
        return currentId++;
    }

    /**
     * Добавление задачи
     *
     * @param task Объект задачи
     */
    public void addTask(Task task) {
        if (taskList.containsValue(task)) {
            System.out.println("Такая задача уже была добавлена");
            return;
        }

        task.setId(getNextId());
        taskList.put(task.getId(), task);
    }

    /**
     * Добавление эпика
     *
     * @param epic Объект эпика
     */
    public void addEpic(Epic epic) {
        if (epicList.containsValue(epic)) {
            System.out.println("Такой эпик уже был добавлен");
            return;
        }

        epic.setId(getNextId());
        epicList.put(epic.getId(), epic);
    }

    /**
     * Добавление подзадачи
     *
     * @param subtask Объект подзадачи
     */
    public void addSubtask(Subtask subtask) {
        if (subtaskList.containsValue(subtask)) {
            if (Objects.equals(subtaskList.values().stream().filter(item -> item.equals(subtask)).findFirst().orElseThrow().getEpicId(), subtask.getEpicId())) {
                System.out.println("Такая подзадача в эпик с id = " + subtask.getEpicId() + " уже была добавлена");
                return;
            }
        }

        subtask.setId(getNextId());
        subtaskList.put(subtask.getId(), subtask);

        epicList.get(subtask.getEpicId()).addNewSubtask(subtask.getId());
        correctEpicStatus(subtask.getEpicId());
    }

    /**
     * Редактирование задачи
     *
     * @param task Обновлённая версия добавленной ранее задачи
     */
    public void editTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Передана задача без id. Невозможно обновить");
            return;
        }
        taskList.put(task.getId(), task);
    }

    /**
     * Редактирование эпика
     *
     * @param epic Обновлённая версия добавленного ранее эпика
     */
    public void editEpic(Epic epic) {
        if (epic.getId() == null) {
            System.out.println("Передан эпик без id. Невозможно обновить");
            return;
        }
        epic.setSubtaskList(epicList.get(epic.getId()).getSubtaskList());
        epicList.put(epic.getId(), epic);
    }

    /**
     * Редактирование подзадачи
     *
     * @param subtask Обновлённая версия добавленной ранее подзадачи
     */
    public void editSubtask(Subtask subtask) {
        if (subtask.getId() == null || subtask.getEpicId() == null) {
            System.out.println("Передана подзадача без id или без привязки к эпику. Невозможно обновить");
            return;
        }
        if (!epicList.containsKey(subtask.getEpicId())) {
            System.out.println("Передана подзадача с привязкой к несуществующему эпику. Невозможно обновить");
            return;
        }
        subtaskList.put(subtask.getId(), subtask);
        correctEpicStatus(subtask.getEpicId());
    }

    /**
     * Корректировка статуса эпика
     *
     * @param epicId Идентификатор эпика
     */
    private void correctEpicStatus(int epicId) {
        int countToDo = 0, countDone = 0;
        List<Integer> currentSubtaskList = epicList.get(epicId).getSubtaskList();

        for (Integer id : currentSubtaskList) {
            switch (subtaskList.get(id).getStatus()) {
                case TO_DO -> countToDo++;
                case DONE -> countDone++;
            }
        }

        int subtaskListSize = currentSubtaskList.size();
        if (countToDo == subtaskListSize) {
            epicList.get(epicId).setStatus(Status.TO_DO);
        } else if (countDone == subtaskListSize) {
            epicList.get(epicId).setStatus(Status.DONE);
        } else {
            epicList.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    /**
     * Получение списка задач
     *
     * @return Список задач
     */
    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    /**
     * Получение списка эпика
     *
     * @return Список эпика
     */
    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    /**
     * Получение списка подзадач
     *
     * @return Список подзадач
     */
    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    /**
     * Получение всех задач, эпиков и подзадач
     *
     * @return Список всех задач, эпиков и подзадач
     */
    public HashMap<Integer, AbstractTask> getAllEntities() {
        HashMap<Integer, AbstractTask> allEntitiesList = new HashMap<>();

        allEntitiesList.putAll(taskList);
        allEntitiesList.putAll(epicList);
        allEntitiesList.putAll(subtaskList);

        return allEntitiesList;
    }

    /**
     * Получение списка подзадач конкретного эпика
     *
     * @param epicId Идентификатор эпика
     * @return Список подзадач
     */
    public List<Subtask> getSubtaskListByEpicId(int epicId) {
        if (!epicList.containsKey(epicId)) {
            System.out.println("Не существует эпика с id = " + epicId);
            return null;
        }

        List<Subtask> subtasksInEpic = new ArrayList<>();
        epicList.get(epicId).getSubtaskList().forEach(id -> subtasksInEpic.add(subtaskList.get(id)));

        return subtasksInEpic;
    }

    /**
     * Удаление всех задач
     */
    public void deleteAllTasks() {
        taskList.clear();
    }

    /**
     * Удаление всех эпиков
     */
    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    /**
     * Удаление всех подзадач
     */
    public void deleteAllSubtasks() {
        epicList.forEach((id, epic) -> {
            epic.getSubtaskList().clear();
        });
        subtaskList.clear();
    }

    /**
     * Удаление подзадачи в конкретном эпике
     *
     * @param epicId Идентификатор эпика
     */
    public void deleteAllSubtasksInEpic(int epicId) {
        if (!epicList.containsKey(epicId)) {
            System.out.println("Не существует эпика с id = " + epicId);
            return;
        }

        epicList.get(epicId).getSubtaskList().forEach(subtaskList::remove);
        epicList.get(epicId).getSubtaskList().clear();
    }

    /**
     * Получение задачи по индентификатору
     *
     * @param id Идентификатор
     * @return Задача
     */
    public Task getTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Не существует задачи с id = " + id);
            return null;
        }

        return taskList.get(id);
    }

    /**
     * Получение эпика по идентификатору
     *
     * @param id Идентификатор
     * @return Эпик
     */
    public Epic getEpicById(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Не существует эпика с id = " + id);
            return null;
        }

        return epicList.get(id);
    }

    /**
     * Получение подзадачи по идентификатору
     *
     * @param id Идентификатор
     * @return Подзадача
     */
    public Subtask getSubtaskById(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Не существует подзадачи с id = " + id);
            return null;
        }

        return subtaskList.get(id);
    }

    /**
     * Удаление конкретной задачи
     *
     * @param id Идентификатор задачи
     */
    public void deleteTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Не существует задачи с id = " + id);
            return;
        }

        taskList.remove(id);
    }

    /**
     * Удаление конкретного эпика
     *
     * @param id Идентификатор эпика
     */
    public void deleteEpicById(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Не существует эпика с id = " + id);
            return;
        }

        deleteAllSubtasksInEpic(id);
        epicList.remove(id);
    }

    /**
     * Удаление конкретной подзадачи
     *
     * @param id Идентификатор подзадачи
     */
    public void deleteSubtaskById(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Не существует подзадачи с id = " + id);
            return;
        }

        int epicId = subtaskList.get(id).getEpicId();
        epicList.get(epicId).getSubtaskList().remove((Integer) id);
        subtaskList.remove(id);
    }

    /**
     * Представление объекта таск-менеджера в виде строки
     *
     * @return Строка, описывающая объект таск-менеджера
     */
    @Override
    public String toString() {
        return "TaskManager { " +
                "currentId = " + currentId + ", " +
                "taskList = " + taskList + ", " +
                "epicList = " + epicList + ", " +
                "subtaskList = " + subtaskList +
                " }";
    }
}
