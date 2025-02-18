package com.taskmanager.service.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.taskmanager.model.Epic;
import com.taskmanager.model.Subtask;
import com.taskmanager.service.exceptions.AlreadyExistsException;
import com.taskmanager.service.exceptions.NotFoundException;
import com.taskmanager.service.exceptions.WithouIdException;
import com.taskmanager.service.managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends ManagerAwareHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String[] path = requestUri.getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (path.length == 2) {
                    sendAllEpics(exchange);
                    return;
                }

                if (path.length == 3 && !path[2].isEmpty()) {
                    sendEpicById(exchange, path[2]);
                    return;
                }

                if (path.length == 4 && !path[2].isEmpty() && path[3].equals("subtasks")) {
                    sendSubtasksInEpic(exchange, path[2]);
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            case "POST" -> {
                if (path.length == 2) {
                    sendAddOrUpdateEpic(exchange);
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
                    sendRemoveEpic(exchange, path[2]);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            default -> {
                sendMethodNotSupported(exchange);
            }

        }
    }

    private void sendAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        String allEpicsStr = gson.toJson(epics);
        sendText(exchange, allEpicsStr);
    }

    private void sendEpicById(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int epicId = Integer.parseInt(pathParamId);
            Epic epic = manager.getEpicById(epicId);
            String taskStr = gson.toJson(epic);
            sendText(exchange, taskStr);
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер эпика из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void sendSubtasksInEpic(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int epicId = Integer.parseInt(pathParamId);
            List<Subtask> subtasks = manager.getSubtaskListByEpicId(epicId);
            String taskStr = gson.toJson(subtasks);
            sendText(exchange, taskStr);
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер эпика из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void sendAddOrUpdateEpic(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();

        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement element = JsonParser.parseString(requestBody);
        if (!element.isJsonObject()) {
            sendErrorRequest(exchange, "Проверьте, что запрос составлен правильно и в формате JSON");
            return;
        }

        if (element.isJsonNull() || element.getAsJsonObject().keySet().isEmpty()) {
            sendErrorRequest(exchange, "Отсутствует описание добавляемого эпика");
            return;
        }

        JsonObject object = element.getAsJsonObject();

        Integer id = object.keySet().contains("id") ? object.get("id").getAsInt() : null;
        String name = object.keySet().contains("name") ? object.get("name").getAsString() : null;
        String description = object.keySet().contains("description") ? object.get("description").getAsString() : null;

        if (name == null) {
            sendErrorRequest(exchange, "Не может быть добавлен эпик без имени, проверьте наличие поля name");
            return;
        }

        Epic newEpic = new Epic(id, name, description);

        try {
            if (id == null) {
                manager.addEpic(newEpic);
            } else {
                manager.editEpic(newEpic);
            }
            sendAddItem(exchange);
        } catch (AlreadyExistsException e) {
            sendErrorRequest(exchange, "Такой эпик уже добавлен");
        } catch (WithouIdException e) {
            sendErrorRequest(exchange, "Невозможно обновить эпик без id");
        }
    }

    private void sendRemoveEpic(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int epicId = Integer.parseInt(pathParamId);
            manager.deleteEpicById(epicId);
            sendText(exchange, "Эпик с id = " + epicId + " удален");
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер эпика из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}

