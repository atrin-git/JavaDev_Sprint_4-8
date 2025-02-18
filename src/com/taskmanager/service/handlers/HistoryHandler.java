package com.taskmanager.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.taskmanager.model.AbstractTask;
import com.taskmanager.service.managers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class HistoryHandler extends ManagerAwareHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String[] path = requestUri.getPath().split("/");

        if (exchange.getRequestMethod().equals("GET")) {
            if (path.length == 2) {
                sendHistory(exchange);
                return;
            }

            sendErrorRequest(exchange, "Запрос сформирован некорректно");
        } else {
            sendMethodNotSupported(exchange);
        }
    }

    private void sendHistory(HttpExchange exchange) throws IOException {
        List<AbstractTask> history = manager.getHistory();
        String historyStr = gson.toJson(history);
        sendText(exchange, historyStr);
    }

}

