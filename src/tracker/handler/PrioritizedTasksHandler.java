package tracker.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.interfaces.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendText(exchange, gson.toJson(prioritizedTasks));
    }
}
