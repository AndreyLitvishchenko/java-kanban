package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempFile;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Override
    public FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("test_tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
        return new FileBackedTaskManager(tempFile);
    }


    @Test
    void shouldSaveAndLoadEmptyFile() {
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    void shouldSaveMultipleTasks() {
        Task task1 = new Task("Task 1", "Описание задачи 1");
        manager.postTask(task1);
        Epic epic1 = new Epic("Epic 1", "Описание эпика 1");
        manager.postEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Описание сабтаска 1", epic1.getId());
        manager.postSubtask(subtask1);
        manager.getTaskId(task1.getId());
        manager.getEpicId(epic1.getId());
        manager.getSubtaskId(subtask1.getId());
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertEquals(3, loadedManager.getHistory().size());
    }

    @Test
    void shouldLoadMultipleTasks() {
        FileBackedTaskManager oldManager = new FileBackedTaskManager(tempFile);
        Task task1 = new Task("Task 1", "Описание задачи 1");
        oldManager.postTask(task1);
        Epic epic1 = new Epic("Epic 1", "Описание эпика 1");
        oldManager.postEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Описание сабтаска 1", epic1.getId());
        oldManager.postSubtask(subtask1);
        oldManager.getTaskId(task1.getId());
        oldManager.getEpicId(epic1.getId());
        oldManager.getSubtaskId(subtask1.getId());
        oldManager.save();
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = newManager.getAllTasks();
        List<Epic> epics = newManager.getAllEpics();
        List<Subtask> subtasks = newManager.getAllSubtasks();
        List<Task> history = newManager.getHistory();
        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        assertEquals(3, history.size());
    }

    @Test
    void testSaveThrowsManagerSaveException() throws IOException {
        File tempDir = File.createTempFile("temp", "dir");
        tempDir.delete();
        tempDir.mkdir();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempDir);
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, manager::save);
        assertTrue(exception.getMessage().contains("Ошибка при сохранении в файл:"));
        tempDir.delete();
    }

    @Test
    void testLoadFromFileThrowsManagerSaveException() throws IOException {
        File tempDir = File.createTempFile("temp", "dir");
        tempDir.delete();
        tempDir.mkdir();
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(tempDir);
        });
        assertTrue(exception.getMessage().contains("Ошибка при чтении файла:"));
        tempDir.delete();
    }
}
