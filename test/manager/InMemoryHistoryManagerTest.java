package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();
    Task task1;
    Task task2;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.postTask(task1);
        taskManager.postTask(task2);
    }

    @Test
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }


    @Test
    void getHistory() {
        List<Task> initialHistory = historyManager.getHistory();
        Assertions.assertEquals(0, initialHistory.size());
        initialHistory.add(task1);
        initialHistory.add(task2);
        Assertions.assertEquals(2, initialHistory.size());
    }

    @Test
    void deleteHistory() {
        historyManager.add(task1);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(task1.getId());
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void historyShouldNotContainDuplicatesAndMaintainOrder() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));

        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void historyRemovalWorksCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));

        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistoryReturnsFreshListEachTime() {
        historyManager.add(task1);
        List<Task> historyFirstCall = historyManager.getHistory();
        historyManager.remove(task1.getId());
        List<Task> historySecondCall = historyManager.getHistory();
        assertEquals(1, historyFirstCall.size());
        assertTrue(historySecondCall.isEmpty());
    }
}