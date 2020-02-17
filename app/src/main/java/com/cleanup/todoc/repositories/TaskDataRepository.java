package com.cleanup.todoc.repositories;

import android.arch.lifecycle.LiveData;

import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Task;

import java.util.List;

public class TaskDataRepository {
    private final TaskDao mTaskDao;

    public TaskDataRepository(TaskDao taskDao) {
        this.mTaskDao = taskDao;
    }

    // CREATE
    public void createTask(Task task) {
        this.mTaskDao.createTask(task);
    }
    // UPDATE
    public void updateTask(Task task) {
        this.mTaskDao.updateTask(task);
    }
    // DELETE
    public void deleteTask(long taskId) {
        this.mTaskDao.deleteTask(taskId);
    }
    // GET
    public LiveData<List<Task>> getTasks() {
        return this.mTaskDao.getTasks();
    }
}
