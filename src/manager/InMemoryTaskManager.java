package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;

    // Управление задачами
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskId(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void postTask(Task task) {
        int id = getId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void patchTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void deleteIdTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    // Управление подзадачами
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer subtaskId : tasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        cleanListSubtaskEpic();
    }

    @Override
    public Subtask getSubtaskId(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void postSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        int subtaskId = getId();
        subtask.setId(subtaskId);
        if (epicId == subtaskId) {
            return;
        }
        subtasks.put(subtaskId, subtask);
        epic.getListSubtask().add(subtaskId);
        updateStatus(epicId);
    }

    @Override
    public void patchSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (newSubtask.getEpicId() == subtaskId) {
            return;
        }
        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(newSubtask.getId(), newSubtask);
            updateStatus(newSubtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtaskId(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> listSubtaskId = epic.getListSubtask();
        listSubtaskId.remove(subtaskId);
        historyManager.remove(id);
        updateStatus(epic.getId());
        subtasks.remove(subtaskId);
    }

    // Управление эпиками
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> allSubtaskIds = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allSubtaskIds.addAll(epic.getListSubtask());
        }

        for (Integer subtaskId : allSubtaskIds) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }

        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void postEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void patchEpic(Epic newEpic) {
        int epicId = newEpic.getId();
        if (epics.containsKey(epicId)) {
            epics.put(epicId, newEpic);
            updateStatus(epicId);
        }
    }

    @Override
    public void deleteEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> listSubtaskId = epic.getListSubtask();
        for (Integer i : listSubtaskId) {
            subtasks.remove(i);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
