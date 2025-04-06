package tracker.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.interfaces.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
        if (path.matches(".*/subtasks/\\d+")) {
            String id = path.split("/")[2];
            Subtask subtask = (Subtask) taskManager.getTaskById(Integer.parseInt(id));
            if (subtask == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(subtask));
        } else {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
                exchange.sendResponseHeaders(201, -1);
            } else {
                taskManager.updateTask(subtask);
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
        exchange.sendResponseHeaders(200, -1); // OK
    }
}
