package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static String toString(Task task) {
        if (task.getType() == TypeTask.TASK) {
            return String.format("%d,%s,%s,%s,%s", task.getId(), TypeTask.TASK, task.getTitle(), task.getStatus(), task.getDescription());
        } else if (task.getType() == TypeTask.EPIC) {
            return String.format("%d,%s,%s,%s,%s", task.getId(), TypeTask.EPIC, task.getTitle(), task.getStatus(), task.getDescription());
        } else {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TypeTask.SUBTASK, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        }
    }
    
    public static String toString(List<Task> historyManager) {
        StringBuilder historyId = new StringBuilder();
        for (Task task : historyManager) {
            historyId.append(String.format("%d, ", task.getId()));
        }
        return historyId.toString();
    }

    public static Task taskFromString(String task) {

        String[] values = task.split(",");
        TypeTask typeTask = TypeTask.valueOf(values[1]);
        if (typeTask.equals(TypeTask.TASK)) {
            int id = Integer.parseInt(values[0]);
            TypeTask type = TypeTask.valueOf(values[1]);
            String title = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];

            Task newTask = new Task(title, description);
            newTask.setId(id);
            newTask.setStatus(status);
            newTask.setType(type);
            return newTask;
        } else if (typeTask.equals(TypeTask.EPIC)) {
            int id = Integer.parseInt(values[0]);
            TypeTask type = TypeTask.valueOf(values[1]);
            String title = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];

            Task epicTask = new Epic(title, description);
            epicTask.setId(id);
            epicTask.setStatus(status);
            epicTask.setType(type);
            return epicTask;
        } else {
            int id = Integer.parseInt(values[0]);
            TypeTask type = TypeTask.valueOf(values[1]);
            String title = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];
            int epicId = Integer.parseInt(values[5]);

            Task subtask = new Subtask(title, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setType(type);
            return subtask;
        }
    }

    public static List<Integer> historyFromString(String history) {
        String[] tasksId = history.split(",");
        List<Integer> historyManager = new ArrayList<>();
        for (String id : tasksId) {
            historyManager.add(Integer.parseInt(id));
        }
        return historyManager;
    }
}
