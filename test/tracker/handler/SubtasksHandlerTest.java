package tracker.handler;

import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.server.HttpTaskServerTest;
import tracker.util.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest extends HttpTaskServerTest {

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        HttpResponse<String> response = sendGetRequest("/subtasks/" + subtask.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendGetRequest("/subtasks/" + subtask.getId());
        assertEquals(TaskStatus.IN_PROGRESS, gson.fromJson(getResponse.body(), Subtask.class).getStatus());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", TaskStatus.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendGetRequest("/subtasks/" + subtask.getId());
        assertEquals(404, getResponse.statusCode());
    }
}