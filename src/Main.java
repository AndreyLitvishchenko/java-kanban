import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 3", "Описание задачи 3");

        taskManager.postTask(task1);
        taskManager.postTask(task2);
        taskManager.postTask(task3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getTaskId(task1.getId()));

        task1.setTitle("Задача 1 обновлена");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.patchTask(task1);
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteIdTask(task1.getId());
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteAllTasks();
        System.out.println(taskManager.getAllTasks());

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.postEpic(epic1);
        taskManager.postEpic(epic2);
        System.out.println(taskManager.getAllEpics());

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2.getId());
        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epic2.getId());
        taskManager.postSubtask(subtask1);
        taskManager.postSubtask(subtask2);
        taskManager.postSubtask(subtask3);
        taskManager.postSubtask(subtask4);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getSubtaskId(subtask4.getId()));

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        taskManager.patchSubtask(subtask1);
        taskManager.patchSubtask(subtask2);
        System.out.println(taskManager.getEpicId(epic1.getId()));

        subtask1.setStatus(Status.DONE);
        taskManager.patchSubtask(subtask1);
        System.out.println(taskManager.getEpicId(epic1.getId()));

        taskManager.deleteSubtaskId(subtask1.getId());
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.patchSubtask(subtask2);
        System.out.println(taskManager.getEpicId(epic1.getId()));
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteAllSubtask();
        System.out.println(taskManager.getAllSubtasks());

        Subtask subtask5 = new Subtask("Сабтаск 5", "Описание 5", epic1.getId());
        taskManager.postSubtask(subtask5);
        epic1.setTitle("Обновление эпика");
        taskManager.patchEpic(epic1);
        System.out.println(taskManager.getEpicId(epic1.getId()));

        taskManager.deleteEpicId(epic1.getId());
        System.out.println(taskManager.getAllEpics());

        taskManager.deleteAllEpics();
        System.out.println(taskManager.getAllEpics());
    }
}
