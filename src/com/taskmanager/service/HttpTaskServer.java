package com.taskmanager.service;

import com.sun.net.httpserver.HttpServer;
import com.taskmanager.service.handlers.*;
import com.taskmanager.service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int PORT = 8083;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public HttpTaskServer(TaskManager manager, Integer port) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void start() {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
