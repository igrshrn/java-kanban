package tracker.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches(".*/epics/\\d+")) {
            String id = path.split("/")[2];
            Epic epic = (Epic) taskManager.getTaskById(Integer.parseInt(id));
            if (epic == null) {
                sendMethodNotAllowed(exchange);
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

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
            exchange.sendResponseHeaders(201, -1);
        } else {
            taskManager.updateTask(epic);
            exchange.sendResponseHeaders(200, -1);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().toString().split("/")[2];
        taskManager.deleteTaskById(Integer.parseInt(id));
        exchange.sendResponseHeaders(200, -1);
    }
}
