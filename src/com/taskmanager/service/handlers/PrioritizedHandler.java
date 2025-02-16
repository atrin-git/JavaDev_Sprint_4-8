package com.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.taskmanager.model.AbstractTask;
import com.taskmanager.service.managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class PrioritizedHandler extends ManagerAwareHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String[] path = requestUri.getPath().split("/");

        if (exchange.getRequestMethod().equals("GET")) {
            if (path.length == 2) {
                sendPrioritized(exchange);
                return;
            }
            sendErrorRequest(exchange, "Запрос сформирован некорректно");
        } else {
            sendMethodNotSupported(exchange);
        }

    }

    private void sendPrioritized(HttpExchange exchange) throws IOException {
        List<AbstractTask> prioritizedTasks = manager.getPrioritizedTasks();
        String prioritizedTasksStr = gson.toJson(prioritizedTasks);
        sendText(exchange, prioritizedTasksStr);
    }

}

