package com.taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmanager.model.Epic;
import com.taskmanager.model.Subtask;
import com.taskmanager.service.managers.Managers;
import com.taskmanager.service.managers.TaskManager;
import com.taskmanager.service.typeadapters.DurationAdapter;
import com.taskmanager.service.typeadapters.LocalDateTimeAdapter;
import com.taskmanager.service.typetokens.EpicListTypeToken;
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

public class EpicHandlerTest {
    private final Integer PORT = 8085;
    private final String endpoint = "/epics";
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
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertTrue(epics.contains(epic));
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertTrue(epicFromResponse.equals(epic));
    }

    @Test
    public void testGetEpicByIdWithoutNumber() throws IOException, InterruptedException {
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
    public void testGetEpicByIdNotNumber() throws IOException, InterruptedException {
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
    public void testGetEpicByIdNotFound() throws IOException, InterruptedException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        String taskForRequest = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getEpics().contains(epic));
    }

    @Test
    public void testAddEpicEmptyBody() throws IOException, InterruptedException {
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
    public void testAddEpicNotJsonObject() throws IOException, InterruptedException {
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
    public void testAddEpicEmptyName() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        epic.setName(null);

        String taskForRequest = gson.toJson(epic);
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
    public void testAddEpicAlreadyExists() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");

        String taskForRequest = gson.toJson(epic);
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
    public void testEditEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        Epic epicForUpdate = new Epic(1, "Test NewName", "Testing task 2");

        String taskForRequest = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskForRequest = gson.toJson(epicForUpdate);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskForRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertTrue(taskManager.getEpics().contains(epicForUpdate));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(taskManager.getEpics().contains(epic));
    }

    @Test
    public void testDeleteEpicNotNumber() throws IOException, InterruptedException {
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
    public void testDeleteEpicNotExisted() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetSubtasksInEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtaskFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertTrue(subtaskFromResponse.contains(subtask));
    }

    @Test
    public void testGetSubtasksInEpicNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/text/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetSubtasksInEpicNotExists() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + endpoint + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
