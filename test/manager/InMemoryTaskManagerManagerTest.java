package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;


class InMemoryTaskManagerManagerTest {
    TaskManager taskManager = Managers.getDefault();
    Task task1;
    Task task2;
    Task task3;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach(){
        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        task3 = new Task("Задача 3", "Описание задачи 3");
        taskManager.postTask(task1);
        taskManager.postTask(task2);
        taskManager.postTask(task3);
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.postEpic(epic1);
        taskManager.postEpic(epic2);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        taskManager.postSubtask(subtask1);
        taskManager.postSubtask(subtask2);
    }

    @Test
    void shouldNotOverrideTaskWithManualId() {
        Task manualTask = new Task("Ручная задача", "Описание");
        manualTask.setId(8);
        taskManager.postTask(manualTask);

        Task autoTask = new Task("Авто задача", "Описание");
        taskManager.postTask(autoTask);

        ArrayList<Task> tasks = taskManager.getAllTasks();

        Assertions.assertEquals(5, tasks.size());
        Assertions.assertNotNull(taskManager.getTaskId(8));
        Assertions.assertNotNull(taskManager.getTaskId(autoTask.getId()));
        Assertions.assertNotEquals(manualTask.getId(), autoTask.getId());
    }

    @Test
    void taskShouldRemainUnchangedAfterAddingToManager() {
        Task task = new Task("Тестовая задача", "Описание задачи");
        task.setStatus(Status.NEW);
        task.setId(10);
        taskManager.postTask(task);
        Task fetchedTask = taskManager.getTaskId(task.getId());
        Assertions.assertNotNull(fetchedTask);
        Assertions.assertEquals(task.getId(), fetchedTask.getId());
        Assertions.assertEquals(task.getTitle(), fetchedTask.getTitle());
        Assertions.assertEquals(task.getDescription(), fetchedTask.getDescription());
        Assertions.assertEquals(task.getStatus(), fetchedTask.getStatus());
    }

    @Test
    void taskShouldBeEqualToItself() {
        Task task = taskManager.getTaskId(task1.getId());
        Assertions.assertEquals(task.getId(), task1.getId());
    }

    @Test
    void getAllTasks() {
        ArrayList<Task> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(3, tasks.size());
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        ArrayList<Task> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, tasks.size());

        List<Task> history = taskManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }

    @Test
    void getTaskId() {
        Task task = taskManager.getTaskId(task1.getId());
        Assertions.assertNotNull(task);
        Assertions.assertEquals(task.getTitle(), task1.getTitle());
    }

    @Test
    void postTask() {
        Task newTask = new Task("Задача 4", "Описание задачи 4");
        taskManager.postTask(newTask);

        ArrayList<Task> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(4, tasks.size());

        Task fetchedTask = taskManager.getTaskId(newTask.getId());
        Assertions.assertNotNull(fetchedTask);
    }

    @Test
    void patchTask() {
        task1.setTitle("Обновление задачи 1");
        taskManager.patchTask(task1);

        Task updatedTask = taskManager.getTaskId(task1.getId());
        Assertions.assertNotNull(updatedTask);
        Assertions.assertEquals("Обновление задачи 1", updatedTask.getTitle());
    }

    @Test
    void deleteIdTask() {
        taskManager.deleteIdTask(task1.getId());
        ArrayList<Task> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.postEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        taskManager.postSubtask(subtask);
        subtask.setEpicId(subtask.getId());
        taskManager.patchSubtask(subtask);
        Subtask fetchedSubtask = taskManager.getSubtaskId(subtask.getId());
        Assertions.assertNotEquals(fetchedSubtask.getId(), fetchedSubtask.getEpicId(),
                "Subtask не может быть своим же эпиком");
    }


    @Test
    void subtaskShouldBeEqualToItself() {
        Subtask subtask = taskManager.getSubtaskId(subtask1.getId());
        Assertions.assertEquals(subtask.getId(), subtask1.getId());
    }

    @Test
    void getAllSubtasks() {
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(2, subtasks.size());
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(0, subtasks.size());

        List<Task> history = taskManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }

    @Test
    void getSubtaskId() {
        Subtask subtask = taskManager.getSubtaskId(subtask1.getId());
        Assertions.assertNotNull(subtask);
        Assertions.assertEquals(subtask.getTitle(), subtask1.getTitle());
    }

    @Test
    void postSubtask() {
        Subtask newSubtask = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());
        taskManager.postSubtask(newSubtask);

        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(3, subtasks.size());
    }

    @Test
    void patchSubtask() {
        subtask1.setTitle("Обновленная подзадача 1");
        taskManager.patchSubtask(subtask1);

        Subtask updatedSubtask = taskManager.getSubtaskId(subtask1.getId());
        Assertions.assertEquals("Обновленная подзадача 1", updatedSubtask.getTitle());
    }

    @Test
    void deleteSubtaskId() {
        taskManager.deleteSubtaskId(subtask1.getId());
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(1, subtasks.size());
    }

    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.postEpic(epic);
        Subtask invalidSubtask =
                new Subtask("Подзадача-эпик", "Попытка добавить эпик как подзадачу", epic.getId());
        invalidSubtask.setId(epic.getId());
        Epic fetchedEpic = taskManager.getEpicId(epic.getId());
        ArrayList<Integer> subtaskIds = fetchedEpic.getListSubtask();
        Assertions.assertTrue(subtaskIds.isEmpty());
    }



    @Test
    void epicShouldBeEqualToItself() {
        Epic epic = taskManager.getEpicId(epic1.getId());
        Assertions.assertEquals(epic.getId(), epic1.getId());
    }

    @Test
    void getAllEpics() {
        ArrayList<Epic> epics = taskManager.getAllEpics();
        Assertions.assertEquals(2, epics.size());
    }

    @Test
    void deleteAllEpics() {
        taskManager.deleteAllEpics();
        ArrayList<Epic> epics = taskManager.getAllEpics();
        Assertions.assertEquals(0, epics.size());

        List<Task> history = taskManager.getHistory();
        Assertions.assertTrue(history.isEmpty());
    }

    @Test
    void getEpicId() {
        Epic epic = taskManager.getEpicId(epic1.getId());
        Assertions.assertNotNull(epic);
        Assertions.assertEquals(epic.getTitle(), epic1.getTitle());
    }

    @Test
    void postEpic() {
        Epic newEpic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.postEpic(newEpic);
        ArrayList<Epic> epics = taskManager.getAllEpics();
        Assertions.assertEquals(3, epics.size());
    }

    @Test
    void patchEpic() {
        epic1.setTitle("Обновленный эпик 1");
        taskManager.patchEpic(epic1);

        Epic updatedEpic = taskManager.getEpicId(epic1.getId());
        Assertions.assertEquals("Обновленный эпик 1", updatedEpic.getTitle());
    }

    @Test
    void deleteEpicId() {
        taskManager.deleteEpicId(epic1.getId());
        ArrayList<Epic> epics = taskManager.getAllEpics();
        Assertions.assertEquals(1, epics.size());
    }
}