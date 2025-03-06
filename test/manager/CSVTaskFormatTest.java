package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import tasks.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVTaskFormatTest {

    @Test
    void testToString() {
        Task task = new Task("Test Task", "Description");
        task.setId(1);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(null);
        task.setEndTime(null);

        String expected = "1," + TypeTask.TASK + ",Test Task,NEW,Description,60,null,null";
        String actual = CSVTaskFormat.toString(task);
        assertEquals(expected, actual);
    }

    @Test
    void testToString1() {
        Task task = new Task("Test Task", "Description");
        task.setId(2);
        task.setDuration(Duration.ofMinutes(30));
        LocalDateTime start = LocalDateTime.of(2025, 3, 4, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 4, 10, 30);
        task.setStartTime(start);
        task.setEndTime(end);

        String expected = "2," + TypeTask.TASK + ",Test Task,NEW,Description,30,"
                + start.toString() + "," + end.toString();
        String actual = CSVTaskFormat.toString(task);
        assertEquals(expected, actual);
    }

    @Test
    void epicToString() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        epic.setId(3);
        epic.setDuration(Duration.ofMinutes(90));
        epic.setStartTime(null);
        epic.setEndTime(null);

        String expected = "3," + TypeTask.EPIC + ",Test Epic,NEW,Epic Description,90,null,null";
        String actual = CSVTaskFormat.toString(epic);
        assertEquals(expected, actual);
    }

    @Test
    void testSubtaskToStringWithNonNullTimes() {
        int epicId = 3;
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epicId);
        subtask.setId(4);
        subtask.setDuration(Duration.ofMinutes(45));
        LocalDateTime start = LocalDateTime.of(2025, 3, 4, 11, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 4, 11, 45);
        subtask.setStartTime(start);
        subtask.setEndTime(end);

        String expected = "4," + TypeTask.SUBTASK + ",Test Subtask,NEW,Subtask Description,"
                + epicId + ",45," + start.toString() + "," + end.toString();
        String actual = CSVTaskFormat.toString(subtask);
        assertEquals(expected, actual);
    }

    @Test
    void testTaskFromStringWithNullTimes() {
        String input = "1,TASK,Task Title,NEW,Task Description,60,null,null";
        Task task = CSVTaskFormat.taskFromString(input);

        assertEquals(1, task.getId());
        assertEquals(TypeTask.TASK, task.getType());
        assertEquals("Task Title", task.getTitle());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("Task Description", task.getDescription());
        assertEquals(Duration.ofMinutes(60), task.getDuration());
        assertNull(task.getStartTime());
        assertNull(task.getEndTime());
    }

    @Test
    void testTaskFromStringWithValidTimes() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 4, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 4, 11, 0);
        String input = String.format("2,TASK,Task Title,NEW,Task Description,30,%s,%s",
                start.toString(), end.toString());
        Task task = CSVTaskFormat.taskFromString(input);

        assertEquals(2, task.getId());
        assertEquals(TypeTask.TASK, task.getType());
        assertEquals("Task Title", task.getTitle());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("Task Description", task.getDescription());
        assertEquals(Duration.ofMinutes(30), task.getDuration());
    }

    @Test
    void testEpicFromStringWithNullTimes() {
        String input = "3,EPIC,Epic Title,NEW,Epic Description,90,null,null";
        Task epic = CSVTaskFormat.taskFromString(input);

        assertEquals(3, epic.getId());
        assertEquals(TypeTask.EPIC, epic.getType());
        assertEquals("Epic Title", epic.getTitle());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(Duration.ofMinutes(90), epic.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
    }

    @Test
    void testEpicFromStringWithValidTimes() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 4, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 4, 13, 0);
        String input = String.format("4,EPIC,Epic Title,NEW,Epic Description,120,%s,%s",
                start.toString(), end.toString());
        Task epic = CSVTaskFormat.taskFromString(input);

        assertEquals(4, epic.getId());
        assertEquals(TypeTask.EPIC, epic.getType());
        assertEquals("Epic Title", epic.getTitle());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(Duration.ofMinutes(120), epic.getDuration());
        assertEquals(start, epic.getStartTime());
        assertEquals(end, epic.getEndTime());
    }

    @Test
    void testSubtaskFromStringWithNullTimes() {
        String input = "5,SUBTASK,Subtask Title,NEW,Subtask Description,3,45,null,null";
        Task subtask = CSVTaskFormat.taskFromString(input);
        assertTrue(subtask instanceof Subtask);
        Subtask st = (Subtask) subtask;

        assertEquals(5, st.getId());
        assertEquals(TypeTask.SUBTASK, st.getType());
        assertEquals("Subtask Title", st.getTitle());
        assertEquals(Status.NEW, st.getStatus());
        assertEquals("Subtask Description", st.getDescription());
        assertEquals(3, st.getEpicId());
        assertEquals(Duration.ofMinutes(45), st.getDuration());
        assertNull(st.getStartTime());
        assertNull(st.getEndTime());
    }

    @Test
    void testSubtaskFromStringWithValidTimes() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 4, 14, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 4, 15, 0);
        String input = String.format("6,SUBTASK,Subtask Title,NEW,Subtask Description,4,50,%s,%s",
                start.toString(), end.toString());
        Task subtask = CSVTaskFormat.taskFromString(input);
        assertTrue(subtask instanceof Subtask);
        Subtask st = (Subtask) subtask;

        assertEquals(6, st.getId());
        assertEquals(TypeTask.SUBTASK, st.getType());
        assertEquals("Subtask Title", st.getTitle());
        assertEquals(Status.NEW, st.getStatus());
        assertEquals("Subtask Description", st.getDescription());
        assertEquals(4, st.getEpicId());
        assertEquals(Duration.ofMinutes(50), st.getDuration());
        assertEquals(start, st.getStartTime());
    }
}