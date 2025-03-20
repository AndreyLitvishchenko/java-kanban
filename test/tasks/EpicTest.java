package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;


class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void updateDurationIfListIsEmpty() {
        Epic emptyEpic = new Epic("Пустой эпик", "Описание пустого эпика");
        taskManager.postEpic(emptyEpic);
        emptyEpic.updateDuration(taskManager.getAllSubtasks());
        assertEquals(Duration.ZERO, emptyEpic.getDuration());
    }

    @Test
    void updateDurationIfListNotEmpty() {
        Epic emptyEpic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.postEpic(emptyEpic);
        Subtask subtask = new Subtask("title", "description", emptyEpic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.MARCH, 1, 10, 0));
        taskManager.postSubtask(subtask);
        emptyEpic.updateDuration(taskManager.getAllSubtasks());
        assertEquals(Duration.ofMinutes(20), emptyEpic.getDuration());
    }

    @Test
    void updateTimeIfListIsEmpty() {
        Epic emptyEpic = new Epic("Пустой эпик", "Описание пустого эпика");
        taskManager.postEpic(emptyEpic);
        emptyEpic.updateTime(taskManager.getAllSubtasks());
        assertNull(emptyEpic.getEndTime());
    }

    @Test
    void updateTimeIfListNotEmpty() {
        Epic emptyEpic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.postEpic(emptyEpic);
        Subtask subtask = new Subtask("title", "description", emptyEpic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.MARCH, 1, 10, 0));
        taskManager.postSubtask(subtask);
        emptyEpic.updateTime(taskManager.getAllSubtasks());
        assertEquals(LocalDateTime.of(2025, 3, 1, 10, 20), emptyEpic.getEndTime());
    }

    @Test
    void updateTimeSetsEarliestStart() {
        Epic epic = new Epic("Epic", "Test Epic");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", epic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.MARCH, 1, 10, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", epic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, Month.MARCH, 1, 9, 30));
        List<Subtask> subtasks = Arrays.asList(subtask1, subtask2);
        epic.updateTime(subtasks);
        assertEquals(LocalDateTime.of(2025, 3, 1, 9, 30), epic.getStartTime());
    }

    @Test
    void updateTimeSetsLatestEnd() {
        Epic epic = new Epic("Epic", "Test Epic");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", epic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", epic.getId(), Duration.ofMinutes(20),
                LocalDateTime.of(2025, 3, 1, 11, 0));
        List<Subtask> subtasks = Arrays.asList(subtask1, subtask2);
        epic.updateTime(subtasks);
        assertEquals(LocalDateTime.of(2025, 3, 1, 11, 20), epic.getEndTime());
    }
}