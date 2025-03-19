package http.handler;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Task;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            List<Task> history = taskManager.getHistory();
            sendText(exchange, gson.toJson(history));
        } else {
            sendNotFound(exchange, "Метод не поддерживается");
        }
    }
}
