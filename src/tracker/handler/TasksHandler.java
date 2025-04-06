package tracker.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.interfaces.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                case null, default -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().toString();
        if (path.matches(".*/tasks/\\d+")) {
            String id = path.split("/")[2];
            Task task = taskManager.getTaskById(Integer.parseInt(id));
            if (task == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(task));
        } else {
            List<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
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
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
            exchange.getResponseBody().write(e.getMessage().getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().toString().split("/")[2];
        taskManager.deleteTaskById(Integer.parseInt(id));
        exchange.sendResponseHeaders(200, -1);
    }
}
