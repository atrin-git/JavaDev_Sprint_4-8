package com.taskmanager.service.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.taskmanager.service.typeadapters.DurationAdapter;
import com.taskmanager.service.typeadapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Отправка общего ответа в случае успеха
     *
     * @param exchange Объект для обмена информацией
     * @param text     Тело ответа
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendMessage(exchange, text, 200);
    }

    /**
     * Отправка общего ответа в случае успеха
     *
     * @param exchange Объект для обмена информацией
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendAddItem(HttpExchange exchange) throws IOException {
        sendMessage(exchange, "", 201);
    }

    /**
     * Отправка ответа в случае, запрос сформирован некорректно
     *
     * @param exchange Объект для обмена информацией
     * @param text     Тело ответа
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendErrorRequest(HttpExchange exchange, String text) throws IOException {
        sendMessage(exchange, text, 400);
    }

    /**
     * Отправка ответа в случае, если объект не был найден
     *
     * @param exchange Объект для обмена информацией
     * @param text     Тело ответа
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendMessage(exchange, text, 404);
    }

    /**
     * Отправка ответа, если использован недействительный метод
     *
     * @param exchange Объект для обмена информацией
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendMethodNotSupported(HttpExchange exchange) throws IOException {
        sendMessage(exchange, "", 405);
    }

    /**
     * Отправка ответа, если при создании или обновлении задача пересекается с уже существующими
     *
     * @param exchange Объект для обмена информацией
     * @param text     Тело ответа
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        sendMessage(exchange, text, 406);
    }

    /**
     * Отправка ответа, если произошла ошибка на стороне сервера
     *
     * @param exchange Объект для обмена информацией
     * @param text     Тело ответа
     * @throws IOException Исключения при отправке ответа
     */
    protected void sendServerError(HttpExchange exchange, String text) throws IOException {
        sendMessage(exchange, text, 500);
    }


    /**
     * Метод для отправки HTTP-ответа
     *
     * @param exchange объект для обмена информацией
     * @param text     текст в тело ответа
     * @param code     код ответа
     * @throws IOException исключения при отправке ответа
     */
    private void sendMessage(HttpExchange exchange, String text, Integer code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
