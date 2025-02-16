package com.taskmanager.service.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.taskmanager.model.Task;
import com.taskmanager.service.exceptions.*;
import com.taskmanager.service.managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends ManagerAwareHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String[] path = requestUri.getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (path.length == 2) {
                    sendAllTasks(exchange);
                    return;
                }

                if (path.length == 3 && !path[2].isEmpty()) {
                    sendTaskById(exchange, path[2]);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            case "POST" -> {
                if (path.length == 2) {
                    sendAddOrUpdateTask(exchange);
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
                    sendRemoveTask(exchange, path[2]);
                    return;
                }

                sendErrorRequest(exchange, "Запрос сформирован некорректно");
            }
            default -> {
                sendMethodNotSupported(exchange);
            }

        }
    }

    private void sendAllTasks(HttpExchange exchange) throws IOException {
        try {
            List<Task> tasks = manager.getTasks();
            String allTasksStr = gson.toJson(tasks);
            sendText(exchange, allTasksStr);
        } catch (ManagerReadException e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void sendTaskById(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int taskId = Integer.parseInt(pathParamId);
            Task task = manager.getTaskById(taskId);
            String taskStr = gson.toJson(task);
            sendText(exchange, taskStr);
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер таски из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (ManagerReadException e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void sendAddOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();

        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement element = JsonParser.parseString(requestBody);
        if (!element.isJsonObject()) {
            sendErrorRequest(exchange, "Проверьте, что запрос составлен правильно и в формате JSON");
            return;
        }


        if (element.isJsonNull() || element.getAsJsonObject().keySet().isEmpty()) {
            sendErrorRequest(exchange, "Отсутствует описание добавляемой задачи");
            return;
        }

        JsonObject object = element.getAsJsonObject();

        Integer id = object.keySet().contains("id") ? object.get("id").getAsInt() : null;
        String name = object.keySet().contains("name") ? object.get("name").getAsString() : null;
        String description = object.keySet().contains("description") ? object.get("description").getAsString() : null;
        LocalDateTime startTime = object.keySet().contains("startTime") ? gson.fromJson(object.get("startTime"), LocalDateTime.class) : null;
        Duration duration = object.keySet().contains("duration") ? gson.fromJson(object.get("duration"), Duration.class) : null;


        Task newTask = new Task(id, name, description, startTime, duration);

        if (newTask.getName() == null) {
            sendErrorRequest(exchange, "Не может быть добавлена задача без имени, проверьте наличие поля name");
            return;
        }

        try {
            if (id == null) {
                manager.addTask(newTask);
            } else {
                manager.editTask(newTask);
            }
            sendAddItem(exchange);
        } catch (AlreadyExistsException e) {
            sendErrorRequest(exchange, "Такая задача уже добавлена");
        } catch (TimeOverlapException e) {
            sendHasInteractions(exchange, "Добавляемая задача имеет пересечение по времени с другими задачами");
        } catch (WithouIdException e) {
            sendErrorRequest(exchange, "Невозможно обновить задачу без id");
        } catch (ManagerSaveException e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void sendRemoveTask(HttpExchange exchange, String pathParamId) throws IOException {
        try {
            int taskId = Integer.parseInt(pathParamId);
            manager.deleteTaskById(taskId);
            sendText(exchange, "Задача с id = " + taskId + " удалена");
        } catch (NumberFormatException e) {
            sendErrorRequest(exchange, "Запрос сформирован некорректно. Не удаётся определить номер таски из " + pathParamId);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}

