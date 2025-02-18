package com.taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmanager.model.Epic;
import com.taskmanager.model.Subtask;
import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;
import com.taskmanager.service.typeadapters.DurationAdapter;
import com.taskmanager.service.typeadapters.LocalDateTimeAdapter;
import com.taskmanager.service.typetokens.SubtaskListTypeToken;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {
    private final Integer PORT = 8085;
    private final String endpoint = "/subtasks";
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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        taskManager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertTrue(subtasks.contains(subtask));
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        taskManager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(subtask, subtaskFromResponse);
    }

    @Test
    public void testGetSubtaskByIdWrongUrl() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "123/123/123");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetSubtaskByIdNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/text");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        String subtaskForRequest = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    public void testAddSubtaskEmptyBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddSubtaskNotJsonObject() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddSubtaskEmptyEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        subtask.setEpicId(null);

        String taskForRequest = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddSubtaskEmptyName() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        subtask.setName(null);

        String taskForRequest = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddSubtaskAlreadyExists() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);

        String taskForRequest = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testEditSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest", 1);
        Subtask subtaskForUpdate = new Subtask(2, "Subtest 2", 1);

        String taskForRequest = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskForRequest = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getSubtasks().contains(subtaskForUpdate));
    }

    @Test
    public void testAddSubtaskTimeOverlap() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Subtask subtask1 = new Subtask("Test", 1, startDate, Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("Test 2", 1, startDate, Duration.ofMinutes(5));

        String taskForRequest = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskForRequest = gson.toJson(subtask2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtest 2", 1);
        taskManager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    public void testDeleteSubtaskNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/text");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testDeleteSubtaskNotExisted() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
