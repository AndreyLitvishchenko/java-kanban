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
import tasks.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtaskHandler(TaskManager taskManager) {
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
        if (path.equals("/subtasks")) {
            if (query != null && query.contains("id=")) {
                String idParam = query.substring(query.indexOf("id=") + 3);
                int id = Integer.parseInt(idParam);
                Subtask subtask = taskManager.getSubtaskId(id);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask));
                } else {
                    throw new NotFoundException("Подзадача с ID " + id + " не найдена");
                }
            } else {
                ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
                sendText(exchange, gson.toJson(subtasks));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/subtasks")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getId() == 0) {
                taskManager.postSubtask(subtask);
                sendCreated(exchange, gson.toJson(subtask));
            } else {
                taskManager.patchSubtask(subtask);
                sendText(exchange, gson.toJson(subtask));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path, String query) throws IOException {
        if (path.equals("/subtasks")) {
            if (query != null && query.contains("id=")) {
                String idParam = query.substring(query.indexOf("id=") + 3);
                int id = Integer.parseInt(idParam);
                taskManager.deleteSubtaskId(id);
                sendText(exchange, "Подзадача с ID " + id + " успешно удалена");
            } else {
                taskManager.deleteAllSubtask();
                sendText(exchange, "Все подзадачи успешно удалены");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }
}
