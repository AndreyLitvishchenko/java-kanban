package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class Epic extends tasks.Task {
    private ArrayList<Integer> listSubtask = new ArrayList<>();
    private TypeTask type = TypeTask.EPIC;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, Duration duration, LocalDateTime startTime) {
        super(title, description);
        endTime = startTime;
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
}
