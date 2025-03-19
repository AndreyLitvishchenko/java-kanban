package http.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exception.NotFoundException;
import exception.TaskInteractionException;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicHandler(TaskManager taskManager) {
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
        if (path.equals("/epics")) {
            if (query != null) {
                if (query.contains("id=")) {
                    String idParam = query.substring(query.indexOf("id=") + 3);
                    int id = Integer.parseInt(idParam);
                    Epic epic = taskManager.getEpicId(id);
                    if (epic != null) {
                        sendText(exchange, gson.toJson(epic));
                    } else {
                        throw new NotFoundException("Эпик с ID " + id + " не найден");
                    }
                } else if (path.contains("/subtasks") && query.contains("epicId=")) {
                    String epicIdParam = query.substring(query.indexOf("epicId=") + 7);
                    int epicId = Integer.parseInt(epicIdParam);
                    List<Subtask> subtasks = taskManager.getEpicId(epicId).getSubtasks();
                    sendText(exchange, gson.toJson(subtasks));
                }
            } else {
                ArrayList<Epic> epics = taskManager.getAllEpics();
                sendText(exchange, gson.toJson(epics));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getId() == 0) {
                taskManager.postEpic(epic);
                sendCreated(exchange, gson.toJson(epic));
            } else {
                taskManager.patchEpic(epic);
                sendText(exchange, gson.toJson(epic));
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path, String query) throws IOException {
        if (path.equals("/epics")) {
            if (query != null && query.contains("id=")) {
                String idParam = query.substring(query.indexOf("id=") + 3);
                int id = Integer.parseInt(idParam);
                taskManager.deleteEpicId(id);
                sendText(exchange, "Эпик с ID " + id + " успешно удален");
            } else {
                taskManager.deleteAllEpics();
                sendText(exchange, "Все эпики успешно удалены");
            }
        } else {
            sendNotFound(exchange, "Неверный путь");
        }
    }
}
