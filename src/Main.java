import http.HttpTaskServer;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();

            System.out.println("Сервер запущен на порту " + HttpTaskServer.PORT);
            System.out.println("Для остановки сервера нажмите CTRL+C");

            TaskManager taskManager = Managers.getDefault();
            HistoryManager historyManager = Managers.getDefaultHistory();

            Task task1 = new Task("Задача 1", "Описание задачи 1");
            Task task2 = new Task("Задача 2", "Описание задачи 2");
            Task task3 = new Task("Задача 3", "Описание задачи 3");

            taskManager.postTask(task1);
            taskManager.postTask(task2);
            taskManager.postTask(task3);

            Epic epic1 = new Epic("Эпик1", "Описание эпика1");
            Epic epic2 = new Epic("Эпик2", "Описание эпика2");
            taskManager.postEpic(epic1);
            taskManager.postEpic(epic2);

            Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1.getId());
            Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1.getId());
            Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2.getId());
            Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epic2.getId());
            taskManager.postSubtask(subtask1);
            taskManager.postSubtask(subtask2);
            taskManager.postSubtask(subtask3);
            taskManager.postSubtask(subtask4);

            taskManager.getTaskId(task1.getId());
            taskManager.getEpicId(epic1.getId());
            taskManager.getSubtaskId(subtask1.getId());

            System.out.println("Созданы тестовые задачи, эпики и подзадачи");
            System.out.println("Доступ к API: http://localhost:8080/tasks");

        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
