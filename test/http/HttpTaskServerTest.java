package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    private static final int TEST_PORT = 8081; // Используем другой порт для тестов
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        TestHttpTaskServer testServer = new TestHttpTaskServer(manager);
        taskServer = testServer;
        gson = testServer.getGson();
        client = HttpClient.newHttpClient();
        baseUrl = "http://localhost:" + TEST_PORT;
        taskServer.start();
    }

    @AfterEach
    void shutDown() {
        if (taskServer != null) {
            taskServer.stop();
        }
    }

    private static class TestHttpTaskServer extends HttpTaskServer {
        public TestHttpTaskServer(TaskManager taskManager) throws IOException {
            super(taskManager);
        }

        public static Gson getGson() {
            return HttpTaskServer.getGson();
        }

        @Override
        public int getPort() {
            return TEST_PORT;
        }
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(task);

        URI url = URI.create(baseUrl + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тестовая задача", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);

        URI url = URI.create(baseUrl + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Тестовая задача", tasks.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        URI url = URI.create(baseUrl + "/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(receivedTask, "Задача не возвращается");
        assertEquals(taskId, receivedTask.getId(), "Некорректный ID задачи");
        assertEquals("Тестовая задача", receivedTask.getTitle(), "Некорректное имя задачи");
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        URI url = URI.create(baseUrl + "/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(0, tasksFromManager.size(), "Задача не была удалена");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        manager.postEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Тестовая подзадача", "Описание тестовой подзадачи", epicId);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create(baseUrl + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Тестовая подзадача", subtasksFromManager.get(0).getTitle(), "Некорректное имя подзадачи");
        assertEquals(epicId, subtasksFromManager.get(0).getEpicId(), "Некорректный ID эпика");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        manager.getTaskId(taskId);

        URI url = URI.create(baseUrl + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество задач в истории");
        assertEquals(taskId, history.get(0).getId(), "Некорректный ID задачи в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStatus(Status.NEW);
        task1.setStartTime(now.plusHours(2));
        manager.postTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStatus(Status.NEW);
        task2.setStartTime(now.plusHours(1));
        manager.postTask(task2);

        URI url = URI.create(baseUrl + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(prioritizedTasks, "Приоритезированные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач");

        assertEquals("Задача 2", prioritizedTasks.get(0).getTitle(), "Неверный порядок задач по приоритету");
    }
}
