package com.cleanup.todoc;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseInstrumentedTest {
    private TodocDatabase mDatabase;
    private ProjectDao mProjectDao;
    private TaskDao mTaskDao;

    private Task mTask = new Task(1,1L,"Task n°1", new Date().getTime());

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getContext();
        //mDatabase = TodocDatabase.getInstance(context);
        mDatabase = Room.inMemoryDatabaseBuilder(context,TodocDatabase.class).build();

        mProjectDao = mDatabase.projectDao();
        mTaskDao = mDatabase.taskDao();
    }
    @After
    public void closeDb() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    @Test
    public void getProjectsFromDatabase() {
        // Add projects in the database
        for (Project project : Project.getAllProjects()) {
            mProjectDao.createProject(project);
        }
        // Get projects from the database
        List<Project> projects = getValue(mProjectDao.getProjects());

        // Check the count of project in the database
        assertEquals(3, projects.size());
    }
    @Test
    public void getProjectByIdFromDatabase() {
        // Add projects in the database
        for (Project project : Project.getAllProjects()) {
            mProjectDao.createProject(project);
        }

        // Get project from the database
        Project project = getValue(mProjectDao.getProject(2L));

        // Check if we get the good project
        assertEquals(2L, project.getId());
        assertEquals("Projet Lucidia", project.getName());
        assertEquals(0xFFB4CDBA, project.getColor());
    }
    @Test
    public void insertAndSelectAndUpdateTaskFromDatabase() {
        // Add a project in the database
        mProjectDao.createProject(Project.getProjectById(1L));
        // Add a task in the database
        mTaskDao.createTask(mTask);
        // Get the list of task from the database
        List<Task> tasks = getValue(mTaskDao.getTasks());

        assertEquals(1, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals("Task n°1", tasks.get(0).getName());

        // Update the name of the task
        mTask.setName("EDIT - Task n°1");
        mTaskDao.updateTask(mTask);
        // Get the list of task from the database
        tasks = getValue(mTaskDao.getTasks());

        assertEquals("EDIT - Task n°1",tasks.get(0).getName());
    }
    @Test
    public void deleteTaskFromDatabase() {
        // Add a project
        mProjectDao.createProject(Project.getProjectById(1L));
        // Add a task
        mTaskDao.createTask(mTask);
        // Delete the task
        mTaskDao.deleteTask(mTask.getId());

        // Get the list of task
        List<Task> tasks = getValue(mTaskDao.getTasks());
        assertEquals(0, tasks.size());
    }

    // GETTING VALUE FROM LIVEDATA
    public static <T> T getValue(final LiveData liveData)  {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (T) data[0];
    }
}