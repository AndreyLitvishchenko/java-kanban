package http;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // Создаём задачу
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        // Конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // Создаём HTTP-запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(201, response.statusCode());

        // Проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тестовая задача", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // Создаём задачу и добавляем её в менеджер
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);

        // Создаём HTTP-запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем содержимое ответа
        ArrayList<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Тестовая задача", tasks.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // Создаём задачу и добавляем её в менеджер
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        // Создаём HTTP-запрос
        URI url = URI.create("http://localhost:8080/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем содержимое ответа
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(receivedTask, "Задача не возвращается");
        assertEquals(taskId, receivedTask.getId(), "Некорректный ID задачи");
        assertEquals("Тестовая задача", receivedTask.getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        // Создаём задачу и добавляем её в менеджер
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        // Создаём HTTP-запрос
        URI url = URI.create("http://localhost:8080/tasks?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что задача удалена
        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(0, tasksFromManager.size(), "Задача не была удалена");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // Создаём эпик и добавляем его в менеджер
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        manager.postEpic(epic);
        int epicId = epic.getId();

        // Создаём подзадачу
        Subtask subtask = new Subtask("Тестовая подзадача", "Описание тестовой подзадачи", epicId);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        // Конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // Создаём HTTP-запрос
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(201, response.statusCode());

        // Проверяем, что создалась одна подзадача с корректным именем
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Тестовая подзадача", subtasksFromManager.get(0).getTitle(), "Некорректное имя подзадачи");
        assertEquals(epicId, subtasksFromManager.get(0).getEpicId(), "Некорректный ID эпика");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // Создаём задачу и добавляем её в менеджер
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setStatus(Status.NEW);
        manager.postTask(task);
        int taskId = task.getId();

        // Получаем задачу по ID, чтобы она попала в историю
        manager.getTaskId(taskId);

        // Создаём HTTP-запрос для получения истории
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем содержимое ответа
        List<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество задач в истории");
        assertEquals(taskId, history.get(0).getId(), "Некорректный ID задачи в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Создаём две задачи с разным временем начала
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStatus(Status.NEW);
        task1.setStartTime(now.plusHours(2));
        manager.postTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStatus(Status.NEW);
        task2.setStartTime(now.plusHours(1));
        manager.postTask(task2);

        // Создаём HTTP-запрос для получения задач по приоритету
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Отправляем запрос
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем содержимое ответа
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(prioritizedTasks, "Приоритезированные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач");

        // Проверяем порядок задач (сначала должна идти задача с более ранним временем
        // начала)
        assertEquals("Задача 2", prioritizedTasks.get(0).getTitle(), "Неверный порядок задач по приоритету");
    }
}
