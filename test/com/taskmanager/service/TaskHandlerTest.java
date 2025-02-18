package com.taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmanager.model.Task;
import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;
import com.taskmanager.service.typeadapters.DurationAdapter;
import com.taskmanager.service.typeadapters.LocalDateTimeAdapter;
import com.taskmanager.service.typetokens.TaskListTypeToken;
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

public class TaskHandlerTest {
    private final Integer PORT = 8085;
    private final String endpoint = "/tasks";
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
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertTrue(tasks.contains(task));
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task, taskFromResponse);
    }

    @Test
    public void testGetTaskByIdWrongRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/123/123");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetTaskByIdNotNumber() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

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
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));

        String taskForRequest = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getTasks().contains(task));
    }

    @Test
    public void testAddTaskEmptyBody() throws IOException, InterruptedException {
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
    public void testAddTaskNotJsonObject() throws IOException, InterruptedException {
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
    public void testAddTaskEmptyName() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        task.setName(null);

        String taskForRequest = gson.toJson(task);
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
    public void testAddTaskAlreadyExists() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));

        String taskForRequest = gson.toJson(task);
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
    public void testAddTaskTimeOverlap() throws IOException, InterruptedException {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Task task1 = new Task("Test", "Testing task", startDate, Duration.ofMinutes(5));
        Task task2 = new Task("Test 2", "Testing task 2", startDate, Duration.ofMinutes(5));

        String taskForRequest = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskForRequest = gson.toJson(task2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testEditTask() throws IOException, InterruptedException {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Task task = new Task("Test 2", "Testing task 2", startDate, Duration.ofMinutes(5));
        Task taskForUpdate = new Task(1, "Test NewName", "Testing task 2", startDate, Duration.ofMinutes(5));

        String taskForRequest = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskForRequest = gson.toJson(taskForUpdate);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getTasks().contains(taskForUpdate));
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(taskManager.getTasks().contains(task));
    }

}
