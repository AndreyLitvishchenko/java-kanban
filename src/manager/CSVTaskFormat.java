package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TypeTask;
import tasks.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CSVTaskFormat {

    public static String toString(Task task) {
        Long duration = (task.getDuration() == null) ? 0 : task.getDuration().toMinutes();
        String startTimeStr = (task.getStartTime() == null) ? "null" : task.getStartTime().toString();
        String endTimeStr = (task.getEndTime() == null) ? "null": task.getEndTime().toString();
        if (task.getType() == TypeTask.TASK) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s", task.getId(), TypeTask.TASK, task.getTitle(), task.getStatus(),
                    task.getDescription(), duration, startTimeStr, endTimeStr);
        } else if (task.getType() == TypeTask.EPIC) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s", task.getId(), TypeTask.EPIC, task.getTitle(), task.getStatus(),
                    task.getDescription(), duration, startTimeStr, endTimeStr);
        } else {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d,%d,%s,%s", subtask.getId(), TypeTask.SUBTASK, subtask.getTitle(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(),
                    duration, startTimeStr, endTimeStr);
        }
    }

    public static String toString(List<Task> historyManager) {
        StringBuilder historyId = new StringBuilder();
        for (int i = 0; i < historyManager.size(); i++) {
            historyId.append(historyManager.get(i).getId());
            if (i < historyManager.size() - 1) {
                historyId.append(",");
            }
        }
        return historyId.toString();
    }

    public static Task taskFromString(String task) {

        String[] values = task.split(",");
        int id = Integer.parseInt(values[0]);
        TypeTask type = TypeTask.valueOf(values[1]);
        String title = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];

        switch (type) {
        case TASK: {
            long duration = Long.parseLong(values[5]);
            String startTimeStr = values[6];
            String endTimeStr = values[7];
            Task newTask = new Task(title, description);
            newTask.setId(id);
            newTask.setStatus(status);
            newTask.setType(type);
            if (duration > 0) {
                newTask.setDuration(Duration.ofMinutes(duration));
            }
            if (!"null".equals(startTimeStr)) {
                newTask.setStartTime(LocalDateTime.parse(startTimeStr));
            }
            if (!"null".equals(startTimeStr)) {
                newTask.setEndTime(LocalDateTime.parse(endTimeStr));
            }
            return newTask;
        }
        case EPIC: {
            long duration = Long.parseLong(values[5]);
            String startTimeStr = values[6];
            String endTimeStr = values[7];
            Task epicTask = new Epic(title, description);
            epicTask.setId(id);
            epicTask.setStatus(status);
            epicTask.setType(type);
            if (duration > 0) {
                epicTask.setDuration(Duration.ofMinutes(duration));
            }
            if (!"null".equals(startTimeStr)) {
                epicTask.setStartTime(LocalDateTime.parse(startTimeStr));
            }
            if (!"null".equals(startTimeStr)) {
                epicTask.setEndTime(LocalDateTime.parse(endTimeStr));
            }
            return epicTask;
        }
        case SUBTASK: {
            int epicId = Integer.parseInt(values[5]);
            long duration = Long.parseLong(values[6]);
            String startTimeStr = values[7];
            String endTimeStr = values[8];
            Task subtask = new Subtask(title, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setType(type);
            if (duration > 0) {
                subtask.setDuration(Duration.ofMinutes(duration));
            }
            if (!"null".equals(startTimeStr)) {
                subtask.setStartTime(LocalDateTime.parse(startTimeStr));
            }
            if (!"null".equals(startTimeStr)) {
                subtask.setEndTime(LocalDateTime.parse(endTimeStr));
            }
            return subtask;
        }
        default:
            throw new IllegalArgumentException("Unknown task type: " + type);
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
