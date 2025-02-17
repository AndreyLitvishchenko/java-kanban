package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            System.out.println("Временный файл: " + tempFile.getAbsolutePath());
            try (var writer = new java.io.BufferedWriter(new java.io.FileWriter(tempFile))) {
                writer.write("id,type,name,status,description,epic");
            }
            FileBackedTaskManager emptyManager = FileBackedTaskManager.loadFromFile(tempFile);
            System.out.println("Пустой менеджер:");
            System.out.println("Задачи: " + emptyManager.getAllTasks());
            System.out.println("Эпики: " + emptyManager.getAllEpics());
            System.out.println("Сабтаски: " + emptyManager.getAllSubtasks());
            System.out.println("История: " + emptyManager.getHistory());
            System.out.println("-----------------------------------------------------");
            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
            Task task1 = new Task("Task 1", "Описание задачи 1");
            manager.postTask(task1);
            Epic epic1 = new Epic("Epic 1", "Описание эпика 1");
            manager.postEpic(epic1);
            Subtask subtask1 = new Subtask("Subtask 1", "Описание сабтаска 1", epic1.getId());
            manager.postSubtask(subtask1);
            System.out.println("Исходный менеджер:");
            System.out.println("Задачи: " + manager.getAllTasks());
            System.out.println("Эпики: " + manager.getAllEpics());
            System.out.println("Сабтаски: " + manager.getAllSubtasks());
            System.out.println("История: " + manager.getHistory());
            System.out.println("-----------------------------------------------------");
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            System.out.println("Загруженный менеджер:");
            System.out.println("Задачи: " + loadedManager.getAllTasks());
            System.out.println("Эпики: " + loadedManager.getAllEpics());
            System.out.println("Сабтаски: " + loadedManager.getAllSubtasks());
            System.out.println("История: " + loadedManager.getHistory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int generatorId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            List<String> taskLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = CSVTaskFormat.taskFromString(line);
                if (task.getId() > generatorId) {
                    generatorId = task.getId();
                }
                manager.addTask(task);
            }
            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isEmpty()) {
                List<Integer> historyIds = CSVTaskFormat.historyFromString(historyLine);
                for (Integer id : historyIds) {
                    Task foundTask = manager.getTaskId(id);
                    if (foundTask == null) {
                        foundTask = manager.getEpicId(id);
                    }
                    if (foundTask == null) {
                        foundTask = manager.getSubtaskId(id);
                    }
                    if (foundTask != null) {
                        manager.getHistory().add(foundTask);
                    }
                }
            }
            manager.setId(generatorId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + e.getMessage());
        }
        return manager;
    }

    private void addTask(Task task) {
        int taskId = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(taskId, task);
                break;
            case EPIC:
                epics.put(taskId, (Epic) task);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(taskId, subtask);
                Epic parentEpic = epics.get(subtask.getEpicId());
                if (parentEpic != null) {
                    parentEpic.getListSubtask().add(taskId);
                }
                break;
        }
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            for (Task task : getAllTasks()) {
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(CSVTaskFormat.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(CSVTaskFormat.toString(subtask));
                writer.newLine();
            }
            writer.newLine();
            writer.write(CSVTaskFormat.toString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteIdTask(int id) {
        super.deleteIdTask(id);
        save();
    }

    @Override
    public void postTask(Task task) {
        super.postTask(task);
        save();
    }

    @Override
    public void patchTask(Task newTask) {
        super.patchTask(newTask);
        save();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
        super.postEpic(epic);
        save();
    }

    @Override
    public void patchEpic(Epic newEpic) {
        super.patchEpic(newEpic);
        save();
    }

    @Override
    public void deleteEpicId(int id) {
        super.deleteEpicId(id);
        save();
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
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void postSubtask(Subtask subtask) {
        super.postSubtask(subtask);
        save();
    }

    @Override
    public void patchSubtask(Subtask newSubtask) {
        super.patchSubtask(newSubtask);
        save();
    }

    @Override
    public void deleteSubtaskId(Integer subtaskId) {
        super.deleteSubtaskId(subtaskId);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
