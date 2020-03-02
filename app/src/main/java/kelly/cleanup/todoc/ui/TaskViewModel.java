package kelly.cleanup.todoc.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import kelly.cleanup.todoc.model.Project;
import kelly.cleanup.todoc.model.Task;
import kelly.cleanup.todoc.repositories.ProjectDataRepository;
import kelly.cleanup.todoc.repositories.TaskDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class TaskViewModel extends ViewModel {
    // REPOSITORIES
    private final ProjectDataRepository mProjectDataRepository;
    private final TaskDataRepository mTaskDataRepository;
    private final Executor mExecutor;

    // DATA
    public TaskViewModel(ProjectDataRepository projectDataRepository, TaskDataRepository taskDataRepository, Executor executor) {
        this.mProjectDataRepository = projectDataRepository;
        this.mTaskDataRepository = taskDataRepository;
        this.mExecutor = executor;
    }

    // FOR PROJECT
    public LiveData<Project> getProject(long projectId) {
        return mProjectDataRepository.getProject(projectId);
    }
    public LiveData<List<Project>> getProjects() {
        return mProjectDataRepository.getProjects();
    }

    // FOR TASK
    public LiveData<List<Task>> getTasks() {
        return mTaskDataRepository.getTasks();
    }
    public void createTask(final Task task) {
        mExecutor.execute(() -> mTaskDataRepository.createTask(task));
    }
    public void updateTask(final Task task) {
        mExecutor.execute(() -> mTaskDataRepository.updateTask(task));
    }
    public void deleteTask(final long taskId) {
        mExecutor.execute(() -> mTaskDataRepository.deleteTask(taskId));
    }
}
