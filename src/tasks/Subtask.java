package tasks;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {
    private int epicId;
    private TypeTask type = TypeTask.SUBTASK;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
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
}
