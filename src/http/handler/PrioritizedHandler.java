package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            List<Task> allTasks = taskManager.getAllTasks();
            allTasks.sort((o1, o2) -> {
                if (o1.getStartTime() == null && o2.getStartTime() == null) {
                    return 0;
                }
                if (o1.getStartTime() == null) {
                    return 1;
                }
                if (o2.getStartTime() == null) {
                    return -1;
                }
                return o1.getStartTime().compareTo(o2.getStartTime());
            });

            sendText(exchange, gson.toJson(allTasks));
        } else {
            sendNotFound(exchange, "Метод не поддерживается");
        }
    }
}
