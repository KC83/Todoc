package com.cleanup.todoc;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.VerificationCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.cleanup.todoc.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @author Gaëtan HERFRAY
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    private TaskDao mTaskDao;
    private TodocDatabase mDatabase;

    private List<Task> mTasks;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getContext();
        mDatabase = TodocDatabase.getInstance(context);
        //mDatabase = Room.inMemoryDatabaseBuilder(context,TodocDatabase.class).build();
        mTaskDao = mDatabase.taskDao();
    }
    @After
    public void closeDb() {
        if (mDatabase != null && mDatabase.isOpen()) {
            //mDatabase.close();
        }
    }

    @Test
    public void addAndRemoveTask() {
        mTaskDao.deleteAllTasks(); // DELETE TABLE TASK

        MainActivity activity = rule.getActivity();
        TextView lblNoTask = activity.findViewById(R.id.lbl_no_task);
        RecyclerView listTasks = activity.findViewById(R.id.list_tasks);

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());

        // Check that lblTask is not displayed anymore
        assertThat(lblNoTask.getVisibility(), equalTo(View.GONE));
        // Check that recyclerView is displayed
        assertThat(listTasks.getVisibility(), equalTo(View.VISIBLE));
        // Check that it contains one element only
        assertThat(listTasks.getAdapter().getItemCount(), equalTo(1));

        // Check if the task is inserted in the database
        mTasks = getValue(mTaskDao.getTasks());
        assertEquals(1, mTasks.size());

        // Click on delete button
        onView(withId(R.id.img_delete)).perform(click());

        // Check if the task is deleted in the database
        mTasks = getValue(mTaskDao.getTasks());
        assertEquals(0, mTasks.size());

        // Check that lblTask is displayed
        assertThat(lblNoTask.getVisibility(), equalTo(View.VISIBLE));
        // Check that recyclerView is not displayed anymore
        assertThat(listTasks.getVisibility(), equalTo(View.GONE));
    }
    @Test
    public void sortTasks() {
        mTaskDao.deleteAllTasks(); // DELETE TABLE TASK
        MainActivity activity = rule.getActivity();

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("zzz Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.txt_task_name)).perform(replaceText("hhh Tâche example"));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name)).check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name)).check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name)).check(matches(withText("hhh Tâche example")));

        // Check if the task is inserted in the database
        mTasks = getValue(mTaskDao.getTasks());
        assertEquals(3, mTasks.size());

        // Sort alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name)).check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name)).check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name)).check(matches(withText("zzz Tâche example")));

        // Sort alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name)).check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name)).check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name)).check(matches(withText("aaa Tâche example")));

        // Sort old first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name)).check(matches(withText("aaa Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name)).check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name)).check(matches(withText("hhh Tâche example")));

        // Sort recent first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name)).check(matches(withText("hhh Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name)).check(matches(withText("zzz Tâche example")));
        onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name)).check(matches(withText("aaa Tâche example")));
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
