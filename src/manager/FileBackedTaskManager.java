package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

//    public static FileBackedTaskManager loadFromFile(File file) {
//        final FileBackedTaskManager manager = new FileBackedTaskManager(file);
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            int generatorId = 0;
//
//            // Читаем строки, пока не дойдём до пустой строки или конца файла
//            while ((line = reader.readLine()) != null && !line.isBlank()) {
//                // Превращаем строку в задачу
//                Task task = CSVTaskFormat.taskFromString(line);
//                if (task != null) {
//                    // Смотрим, какой это тип задачи
//                    switch (task.getType()) {
//                        case TASK:
//                            manager.tasks.put(task.getId(), task);
//                            break;
//                        case EPIC:
//                            manager.epics.put(task.getId(), (Epic) task);
//                            break;
//                        case SUBTASK:
//                            // Кладём в subtasks
//                            Subtask sub = (Subtask) task;
//                            manager.subtasks.put(sub.getId(), sub);
//                            // Привязываем subtask к его эпику
//                            Epic epic = manager.epics.get(sub.getEpicId());
//                            if (epic != null) {
//                                epic.getListSubtask().add(sub.getId());
//                            }
//                            break;
//                    }
//                    // Обновляем maxId
//                    if (task.getId() > maxId) {
//                        maxId = task.getId();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerSaveException("Ошибка при загрузке из файла: " + e.getMessage());
//        }
//    }

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
    public void deleteIdTask(int id) {
        super.deleteIdTask(id);
        save();
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
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }
}
