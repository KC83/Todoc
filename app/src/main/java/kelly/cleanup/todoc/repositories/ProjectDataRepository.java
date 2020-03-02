package kelly.cleanup.todoc.repositories;

import android.arch.lifecycle.LiveData;

import kelly.cleanup.todoc.database.dao.ProjectDao;
import kelly.cleanup.todoc.model.Project;

import java.util.List;

public class ProjectDataRepository {
    private final ProjectDao mProjectDao;

    public ProjectDataRepository(ProjectDao projectDao) {
        this.mProjectDao = projectDao;
    }

    // GET PROJECT
    public LiveData<Project> getProject(long projectId) {
        return this.mProjectDao.getProject(projectId);
    }
    public LiveData<List<Project>> getProjects() {
        return this.mProjectDao.getProjects();
    }
}
