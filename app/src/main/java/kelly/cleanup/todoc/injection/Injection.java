package kelly.cleanup.todoc.injection;

import android.content.Context;

import kelly.cleanup.todoc.database.TodocDatabase;
import kelly.cleanup.todoc.repositories.ProjectDataRepository;
import kelly.cleanup.todoc.repositories.TaskDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {
    public static ProjectDataRepository provideProjectDataRepository(Context context) {
        TodocDatabase database = TodocDatabase.getInstance(context);
        return new ProjectDataRepository(database.projectDao());
    }
    public static TaskDataRepository provideTaskDataRepository(Context context) {
        TodocDatabase database = TodocDatabase.getInstance(context);
        return new TaskDataRepository(database.taskDao());
    }
    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        ProjectDataRepository projectDataRepository = provideProjectDataRepository(context);
        TaskDataRepository taskDataRepository = provideTaskDataRepository(context);
        Executor executor = provideExecutor();

        return new ViewModelFactory(projectDataRepository,taskDataRepository,executor);
    }
}
