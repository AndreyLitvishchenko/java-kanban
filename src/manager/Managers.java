package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManagerManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
