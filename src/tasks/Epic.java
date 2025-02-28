package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> listSubtask = new ArrayList<>();
    private TypeTask type = TypeTask.EPIC;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.endTime = startTime;
    }

    public ArrayList<Integer> getListSubtask() {
        return listSubtask;
    }

    public void setListSubtask(ArrayList<Integer> listSubtask) {
        this.listSubtask = listSubtask;
    }

    @Override
    public TypeTask getType() {
        return type;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateDuration(List<Subtask> subtaskList) {
        if (listSubtask.isEmpty()) {
            setDuration(Duration.ZERO);
            return;
        }

        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtaskList) {
            totalDuration = totalDuration.plus(subtask.getDuration());
        }
    }

    public void updateTime(List<Subtask> subtaskList) {
        if (subtaskList.isEmpty()) {
            setStartTime(null);
            setEndTime(null);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Integer subtaskId : listSubtask) {
            Subtask subtask = subtaskList.get(subtaskId);
            if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                earliestStart = subtask.getStartTime();
            }
            LocalDateTime subtaskEnd = subtask.getEndTime();
            if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                latestEnd = subtaskEnd;
            }
        }
        setStartTime(earliestStart);
        setEndTime(latestEnd);
    }
}
