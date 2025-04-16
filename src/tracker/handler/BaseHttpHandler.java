package tracker.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.exceptions.TaskOverlapException;
import tracker.util.GsonProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = GsonProvider.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> processGet(exchange, exchange.getRequestURI().toString());
                case "POST" -> processPost(exchange);
                case "DELETE" -> processDelete(exchange);
                case null, default -> sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void processGet(HttpExchange exchange, String path) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
        exchange.getResponseBody().write("Метод не поддерживается".getBytes());
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange, TaskOverlapException e) throws IOException {
        exchange.sendResponseHeaders(406, -1);
        exchange.getResponseBody().write(e.getMessage().getBytes());
        exchange.close();
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}
