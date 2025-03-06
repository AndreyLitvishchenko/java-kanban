package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> listSubtask = new ArrayList<>();
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

    public void updateDuration(List<Subtask> subtaskList) {
        if (subtaskList.isEmpty()) {
            setDuration(Duration.ZERO);
            return;
        }

        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtaskList) {
            Duration d = subtask.getDuration();
            if (d == null) {
                d = Duration.ZERO;
            }
            totalDuration = totalDuration.plus(d);
        }
        setDuration(totalDuration);
    }

    public void updateTime(List<Subtask> subtaskList) {
        if (subtaskList.isEmpty()) {
            setStartTime(null);
            setEndTime(null);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtaskList) {
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }
}
