package tracker.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.interfaces.TaskManager;
import tracker.util.GsonProvider;
import tracker.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    protected HttpTaskServer server;
    protected HttpClient client;
    protected TaskManager taskManager;
    protected Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();
        gson = GsonProvider.getGson();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testServerStartAndStop() {
        assertEquals(200, sendGetRequest("/tasks").statusCode());
    }

    public HttpResponse<String> sendGetRequest(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080" + path))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}