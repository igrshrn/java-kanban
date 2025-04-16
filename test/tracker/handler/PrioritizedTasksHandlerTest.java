package tracker.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.exceptions.TaskOverlapException;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;
import tracker.util.TaskStatus;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrioritizedTasksHandlerTest extends HttpTaskServerTest {

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException, TaskOverlapException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpResponse<String> response = sendGetRequest("/prioritized");
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testPrioritizedTasksOrder() throws IOException, InterruptedException, TaskOverlapException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpResponse<String> response = sendGetRequest("/prioritized");
        assertEquals(200, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals("Task 2", tasks.get(1).getTitle());
    }
}