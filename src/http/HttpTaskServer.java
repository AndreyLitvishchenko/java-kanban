package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handler.BaseHttpHandler;
import http.handler.EpicHandler;
import http.handler.HistoryHandler;
import http.handler.PrioritizedHandler;
import http.handler.SubtaskHandler;
import http.handler.TaskHandler;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress("localhost", getPort()), 0);

        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + getPort());
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public static Gson getGson() {
        return BaseHttpHandler.gson;
    }

    public int getPort() {
        return PORT;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
