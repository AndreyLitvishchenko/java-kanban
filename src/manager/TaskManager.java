package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Управление задачами
    ArrayList<tasks.Task> getAllTasks();

    void deleteAllTasks();

    tasks.Task getTaskId(int taskId);

    void postTask(tasks.Task task);

    void patchTask(tasks.Task newTask);

    void deleteIdTask(int id);

    // Управление подзадачами
    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtask();

    Subtask getSubtaskId(int subtaskId);

    void postSubtask(Subtask subtask);

    void patchSubtask(Subtask newSubtask);

    void deleteSubtaskId(Integer subtaskId);

    // Управление эпиками
    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicId(int epicId);

    void postEpic(Epic epic);

    void patchEpic(Epic newEpic);

    void deleteEpicId(int id);
}
