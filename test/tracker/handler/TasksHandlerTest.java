package tracker.handler;

import org.junit.jupiter.api.Test;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;
import tracker.util.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest extends HttpTaskServerTest {

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        HttpResponse<String> response = sendGetRequest("/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendGetRequest("/tasks/" + task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, gson.fromJson(getResponse.body(), Task.class).getStatus());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpResponse<String> getResponse = sendGetRequest("/tasks/" + task.getId());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    public void testTaskOverlap() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));
        String json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }
}