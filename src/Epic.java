import java.util.ArrayList;
import java.util.List;

/**
 * Дата-класс для эпика
 */
public class Epic extends AbstractTask {
    /**
     * Список подзадач эпика
     */
    private List<Integer> subtaskList;

    /**
     * Конструктор копирования
     *
     * @param epic Объект, для которого требуется создать копию
     */
    public Epic(Epic epic) {
        super(epic);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param name Наименование
     */
    public Epic(String name) {
        super(name);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param name        Наименование
     * @param description Описание
     */
    public Epic(String name, String description) {
        super(name, description);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param id   Идентификатор
     * @param name Наименование
     */
    public Epic(int id, String name) {
        super(id, name);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Конструктор
     *
     * @param id          Идентификатор
     * @param name        Наименование
     * @param description Описание
     */
    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtaskList = new ArrayList<>();
    }

    /**
     * Получение списка подзадач
     *
     * @return Список подзадач
     */
    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

    /**
     * Установка списка подзадач
     *
     * @param subtaskList Список подзадач
     */
    public void setSubtaskList(List<Integer> subtaskList) {
        this.subtaskList = subtaskList;
    }

    /**
     * Добавление новой подзадачи в список
     *
     * @param id Идентификатор подзадачи
     */
    public void addNewSubtask(int id) {
        subtaskList.add(id);
    }

    /**
     * Представление объекта эпика в виде строки
     *
     * @return Строка, описывающая объект эпика
     */
    @Override
    public String toString() {
        return "Epic { " +
                "subtaskList = " + subtaskList + ", " +
                "id = " + getId() + ", " +
                "name = \"" + getName() + "\", " +
                "description = \"" + getDescription() + "\", " +
                "status = " + getStatus() +
                " }";
    }
}
