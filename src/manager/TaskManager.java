package manager;

import java.util.ArrayList;
import java.util.HashMap;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int id;

    // Управление задачами
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskId(int taskId) {
        return tasks.get(taskId);
    }

    public void postTask(Task task) {
        int id = getId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void patchTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    public void deleteIdTask(int id) {
        tasks.remove(id);
    }

    // Управление подзадачами
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtask() {
        subtasks.clear();
        cleanListSubtaskEpic();
    }

    public Subtask getSubtaskId(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void postSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            int id = getId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getListSubtask().add(id);
            updateStatus(epicId);
        }
    }

    public void patchSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(newSubtask.getId(), newSubtask);
            updateStatus(newSubtask.getEpicId());
        }
    }

    public void deleteSubtaskId(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> listSubtaskId = epic.getListSubtask();
        listSubtaskId.remove(subtaskId);
        updateStatus(epic.getId());
        subtasks.remove(subtaskId);
    }

    // Управление эпиками
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicId(int epicId) {
        return epics.get(epicId);
    }

    public void postEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void patchEpic(Epic newEpic) {
        int epicId = newEpic.getId();
        if (epics.containsKey(epicId)) {
            epics.put(epicId, newEpic);
            updateStatus(epicId);
        }
    }

    public void deleteEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> listSubtaskId = epic.getListSubtask();
        for (Integer i : listSubtaskId) {
            subtasks.remove(i);
        }
        epics.remove(id);
    }

    private int getId() {
        id += 1;
        return id;
    }

    private void cleanListSubtaskEpic() {
        for (Epic epic : epics.values()) {
            epic.getListSubtask().clear();
            epic.setStatus(Status.NEW);
        }
    }

    private void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getListSubtask().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Integer subtaskId : epic.getListSubtask()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
