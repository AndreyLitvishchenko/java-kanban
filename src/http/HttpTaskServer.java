package http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;

import http.handler.EpicHandler;
import http.handler.HistoryHandler;
import http.handler.PrioritizedHandler;
import http.handler.SubtaskHandler;
import http.handler.TaskHandler;
import manager.Managers;
import manager.TaskManager;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private static final Gson gson = createGson();

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        // Настраиваем обработчики для каждого эндпоинта
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Настройка адаптеров для сериализации/десериализации LocalDateTime и Duration
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            @Override
            public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                if (localDateTime == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(formatter.format(localDateTime));
                }
            }

            @Override
            public LocalDateTime read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                String dateString = jsonReader.nextString();
                return LocalDateTime.parse(dateString, formatter);
            }
        });

        gsonBuilder.registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
            @Override
            public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                if (duration == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(duration.toMinutes());
                }
            }

            @Override
            public Duration read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                long minutes = jsonReader.nextLong();
                return Duration.ofMinutes(minutes);
            }
        });

        return gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
