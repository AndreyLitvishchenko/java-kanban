package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
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
}