package tracker.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
        if (path.matches(".*/epics/\\d+")) {
            String id = path.split("/")[2];
            Epic epic = (Epic) taskManager.getTaskById(Integer.parseInt(id));
            if (epic == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(epic));
        } else if (path.matches(".*/epics/\\d+/subtasks")) {
            String id = path.split("/")[2];
            List<Subtask> subtasks = taskManager.getSubtasksOfEpic(Integer.parseInt(id));
            sendText(exchange, gson.toJson(subtasks));
        } else {
            List<Epic> epics = taskManager.getAllEpics();
            sendText(exchange, gson.toJson(epics));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
            exchange.sendResponseHeaders(201, -1); // Created
        } else {
            taskManager.updateTask(epic);
            exchange.sendResponseHeaders(200, -1); // OK
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().toString().split("/")[2];
        taskManager.deleteTaskById(Integer.parseInt(id));
        exchange.sendResponseHeaders(200, -1); // OK
    }
}
