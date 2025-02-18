package com.taskmanager.service;

import com.google.gson.*;
import com.taskmanager.model.Epic;
import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;
import com.taskmanager.service.typeadapters.DurationAdapter;
import com.taskmanager.service.typeadapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    private final Integer PORT = 8085;
    private final String endpoint = "/history";
    private final TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer server;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        server = new HttpTaskServer(taskManager, PORT);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testNotSupportedMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }


    @Test
    public void testGetHistortWrongRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "123/123");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        taskManager.getEpicById(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement element = JsonParser.parseString(response.body());
        JsonObject object = element.getAsJsonArray().get(0).getAsJsonObject();

        Epic epicFromResponse = gson.fromJson(object.toString(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(epic, epicFromResponse);
    }
}