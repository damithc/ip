package duke.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the operations provided by {@link TaskList}.
 */
public class TaskListTest {
    /**
     * Verifies that a newly created task list starts empty.
     */
    @Test
    public void newTaskListHasNoTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    /**
     * Verifies that tasks are added in insertion order.
     */
    @Test
    public void addAppendsTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Task("return book");

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    /**
     * Verifies that {@code get} uses zero-based indexes to retrieve tasks.
     */
    @Test
    public void getReturnsTaskAtRequestedIndex() {
        TaskList taskList = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Task("return book");
        taskList.add(firstTask);
        taskList.add(secondTask);

        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    /**
     * Verifies that removing a task returns it and updates the list size.
     */
    @Test
    public void removeReturnsRemovedTaskAndShrinksList() {
        TaskList taskList = new TaskList();
        Task firstTask = new Task("read book");
        Task secondTask = new Task("return book");
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task removedTask = taskList.remove(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.get(0));
    }

    /**
     * Verifies that {@code size} reflects additions and removals.
     */
    @Test
    public void sizeReturnsCurrentNumberOfTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        taskList.add(new Task("read book"));
        assertEquals(1, taskList.size());
        taskList.add(new Task("return book"));
        assertEquals(2, taskList.size());
        taskList.remove(0);
        assertEquals(1, taskList.size());
    }

    /**
     * Verifies that only indexes pointing to existing tasks are accepted.
     */
    @Test
    public void isValidIndexRecognisesExistingTaskIndexes() {
        TaskList taskList = new TaskList();

        assertFalse(taskList.isValidIndex(-1));
        assertFalse(taskList.isValidIndex(0));

        taskList.add(new Task("read book"));
        taskList.add(new Task("return book"));

        assertTrue(taskList.isValidIndex(0));
        assertTrue(taskList.isValidIndex(1));
        assertFalse(taskList.isValidIndex(2));
    }

    /**
     * Verifies that {@code markAsDone} changes the selected task's status.
     */
    @Test
    public void markAsDoneMarksTaskAtRequestedIndex() {
        TaskList taskList = new TaskList();
        Task task = new Task("read book");
        taskList.add(task);

        taskList.markAsDone(0);

        assertTrue(task.isDone());
    }

    /**
     * Verifies that {@code unmark} changes a completed task back to incomplete.
     */
    @Test
    public void unmarkMarksTaskAtRequestedIndexAsNotDone() {
        TaskList taskList = new TaskList();
        Task task = new Task("read book");
        task.markAsDone();
        taskList.add(task);

        taskList.unmark(0);

        assertFalse(task.isDone());
    }
}
