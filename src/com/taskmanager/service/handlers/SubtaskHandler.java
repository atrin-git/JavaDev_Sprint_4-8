package com.taskmanager.service.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.taskmanager.model.Subtask;
import com.taskmanager.service.managers.TaskManager;
import com.taskmanager.service.exceptions.AlreadyExistsException;
import com.taskmanager.service.exceptions.NotFoundException;
import com.taskmanager.service.exceptions.TimeOverlapException;
import com.taskmanager.service.exceptions.WithouIdException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends ManagerAwareHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String[] path = requestUri.getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (path.length == 2) {
                    sendAllSubtasks(exchange);
                    return;
                }

                if (path.length == 3 && !path[2].isEmpty()) {
                    sendSubtaskById(exchange, path[2]);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            case "POST" -> {
                if (path.length == 2) {
                    sendAddOrUpdateSubtask(exchange);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            case "DELETE" -> {
                if (path.length == 2) {
                    sendErrorRequest(exchange, "Не передан id, который нужно удалить");
                    return;
                }

                if (path.length == 3 && !path[2].isEmpty()) {
                    sendRemoveSubtask(exchange, path[2]);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            default -> {
                sendMethodNotSupported(exchange);
            }

        }
    }

    private void sendAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> tasks = manager.getSubtasks();
        String allTasksStr = gson.toJson(tasks);
        sendText(exchange, allTasksStr);
    }

    private void sendSubtaskById(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int subtaskId = Integer.parseInt(pathParamId);
            Subtask subtask = manager.getSubtaskById(subtaskId);
            String taskStr = gson.toJson(subtask);
            sendText(exchange, taskStr);
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер подзадачи из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void sendAddOrUpdateSubtask(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();

        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement element = JsonParser.parseString(requestBody);
        if (!element.isJsonObject()) {
            sendErrorRequest(exchange, "Проверьте, что запрос составлен правильно и в формате JSON");
            return;
        }


        if (element.isJsonNull() || element.getAsJsonObject().keySet().isEmpty()) {
            sendErrorRequest(exchange, "Отсутствует описание добавляемой подзадачи");
            return;
        }

        JsonObject object = element.getAsJsonObject();

        Integer id = object.keySet().contains("id") ? object.get("id").getAsInt() : null;
        String name = object.keySet().contains("name") ? object.get("name").getAsString() : null;
        Integer epicId = object.keySet().contains("epicId") ? object.get("epicId").getAsInt() : null;
        String description = object.keySet().contains("description") ? object.get("description").getAsString() : null;
        LocalDateTime startTime = object.keySet().contains("startTime") ? gson.fromJson(object.get("startTime"), LocalDateTime.class) : null;
        Duration duration = object.keySet().contains("duration") ? gson.fromJson(object.get("duration"), Duration.class) : null;

        if (epicId == null) {
            sendErrorRequest(exchange, "Не может быть добавлена подзадача без указания эпика, проверьте поле epicId");
            return;
        }

        if (name == null) {
            sendErrorRequest(exchange, "Не может быть добавлена подзадача без имени, проверьте наличие поля name");
            return;
        }

        Subtask newSubtask = new Subtask(id, name, description, epicId, startTime, duration);

        try {
            if (id == null) {
                manager.addSubtask(newSubtask);
            } else {
                manager.editSubtask(newSubtask);
            }
            sendAddItem(exchange);
        } catch (AlreadyExistsException e) {
            sendErrorRequest(exchange, "Такая подзадача уже добавлена");
        } catch (TimeOverlapException e) {
            sendHasInteractions(exchange, "Добавляемая подзадача имеет пересечение по времени с другими задачами");
        } catch (WithouIdException e) {
            sendErrorRequest(exchange, "Невозможно обновить подзадачу без id");
        } catch (NotFoundException e) {
            sendErrorRequest(exchange, "Не найден эпик с id = " + epicId);
        }
    }

    private void sendRemoveSubtask(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int taskId = Integer.parseInt(pathParamId);
            manager.deleteSubtaskById(taskId);
            sendText(exchange, "Подзадача с id = " + taskId + " удалена");
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер подзадачи из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}

