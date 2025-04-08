package tracker.handler;

import org.junit.jupiter.api.Test;
import tracker.exceptions.TaskOverlapException;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;
import tracker.util.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryHandlerTest extends HttpTaskServerTest {

    @Test
    public void testGetHistory() throws TaskOverlapException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        HttpResponse<String> response = sendGetRequest("/history");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testHistoryAfterDelete() throws IOException, InterruptedException, TaskOverlapException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = sendGetRequest("/history");
        assertEquals(200, response.statusCode());
        assert !response.body().isEmpty();
    }
}