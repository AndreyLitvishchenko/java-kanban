package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(new TaskComparator());
    protected int id;

    // Управление задачами
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(tasks.get(taskId));
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
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void patchTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(newTask.getId(), newTask);
            if (newTask.getStartTime() != null) {
                prioritizedTasks.add(newTask);
            }
        }
    }

    @Override
    public void deleteIdTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    // Управление подзадачами
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(subtaskId));
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
        epic.updateDuration(getAllSubtasks());
        epic.updateTime(getAllSubtasks());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
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
            Epic epic = epics.get(newSubtask.getEpicId());
            if (epic != null) {
                epic.updateDuration(getAllSubtasks());
                epic.updateTime(getAllSubtasks());
            }
            if (newSubtask.getStartTime() != null) {
                prioritizedTasks.add(newSubtask);
            }
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
        epic.updateDuration(getAllSubtasks());
        epic.updateTime(getAllSubtasks());
        prioritizedTasks.remove(subtask.getId());
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
            newEpic.updateDuration(getAllSubtasks());
            newEpic.updateTime(getAllSubtasks());
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    public void setId(int newId) {
        this.id = newId;
    }

    private static class TaskComparator implements Comparator<Task> {

        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return Integer.compare(t1.getId(), t2.getId());
            }

            if (t1.getStartTime() == null) {
                return 1;
            }

            if (t2.getStartTime() == null) {
                return -1;
            }

            int cmp = t1.getStartTime().compareTo(t2.getStartTime());
            return cmp != 0 ? cmp : Integer.compare(t1.getId(), t2.getId());
        }
    }

}
