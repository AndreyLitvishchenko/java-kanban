package http.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exception.NotFoundException;
import exception.TaskInteractionException;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Task;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    handleGetRequest(exchange, path, query);
                    break;
                case "POST":
                    handlePostRequest(exchange, path);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, path, query);
                    break;
                default:
                    sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TaskInteractionException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path, String query) throws IOException {
        if (path.equals("/tasks")) {
            if (query != null && query.contains("id=")) {
                String idParam = query.substring(query.indexOf("id=") + 3);
                int id = Integer.parseInt(idParam);
                Task task = taskManager.getTaskId(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    throw new NotFoundException("Задача с ID " + id + " не найдена");
                }
            } else {
                ArrayList<Task> tasks = taskManager.getAllTasks();
                sendText(exchange, gson.toJson(tasks));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() == 0) {
                taskManager.postTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                taskManager.patchTask(task);
                sendText(exchange, gson.toJson(task));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path, String query) throws IOException {
        if (path.equals("/tasks")) {
            if (query != null && query.contains("id=")) {
                String idParam = query.substring(query.indexOf("id=") + 3);
                int id = Integer.parseInt(idParam);
                taskManager.deleteIdTask(id);
                sendText(exchange, "Задача с ID " + id + " успешно удалена");
            } else {
                taskManager.deleteAllTasks();
                sendText(exchange, "Все задачи успешно удалены");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }
}
