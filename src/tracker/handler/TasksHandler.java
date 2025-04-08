package tracker.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.exceptions.TaskOverlapException;
import tracker.interfaces.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches(".*/tasks/\\d+")) {
            String id = path.split("/")[2];
            Task task = taskManager.getTaskById(Integer.parseInt(id));
            if (task == null) {
                sendMethodNotAllowed(exchange);
                return;
            }
            sendText(exchange, gson.toJson(task));
        } else {
            List<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == 0) {
                taskManager.addTask(task);
                exchange.sendResponseHeaders(201, -1);
            } else {
                taskManager.updateTask(task);
                exchange.sendResponseHeaders(200, -1);
            }
        } catch (TaskOverlapException e) {
            sendHasInteractions(exchange, e);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
            exchange.getResponseBody().write(e.getMessage().getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().toString().split("/")[2];
        taskManager.deleteTaskById(Integer.parseInt(id));
        exchange.sendResponseHeaders(200, -1);
    }
}
