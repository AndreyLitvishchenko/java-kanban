package tasks;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            return;
        }
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }
}
