package chatty.task;

import java.util.ArrayList;
import java.util.List;

import static chatty.util.Constants.REGEX_MATCH_ALL_CHARACTER;

public class TaskList {

    private List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Task deleteTask(int idx) {
        Task task = tasks.get(idx);
        tasks.remove(idx);
        return task;
    }

    public int getTotalTaskNum() {
        return tasks.size();
    }

    public Task getTaskAtIdx(int idx) {
        return tasks.get(idx);
    }

    public Task markTaskAsDone(int idx) {
        Task task = tasks.get(idx);
        task.markAsDone();
        return task;
    }

    public TaskList findTaskWithKeyword(String keyword) {
        TaskList taskListWithKeyword = new TaskList();
        for (Task task : tasks) {
            if (task.getDescription().matches(REGEX_MATCH_ALL_CHARACTER + keyword + REGEX_MATCH_ALL_CHARACTER)) {
                taskListWithKeyword.addTask(task);
            }
        }
        return taskListWithKeyword;
    }
}
