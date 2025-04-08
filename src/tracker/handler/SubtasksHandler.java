package tracker.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.exceptions.TaskOverlapException;
import tracker.interfaces.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches(".*/subtasks/\\d+")) {
            String id = path.split("/")[2];
            Subtask subtask = (Subtask) taskManager.getTaskById(Integer.parseInt(id));
            if (subtask == null) {
                sendMethodNotAllowed(exchange);
                return;
            }
            sendText(exchange, gson.toJson(subtask));
        } else {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
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
