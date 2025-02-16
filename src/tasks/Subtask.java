package tasks;

public class Subtask extends tasks.Task {
    private int epicId;
    private TypeTask tyoe = TypeTask.SUBTASK;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public TypeTask getTyoe() {
        return tyoe;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            return;
        }
        this.epicId = epicId;
    }
}
