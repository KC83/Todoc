package com.cleanup.todoc.repositories;

import android.arch.lifecycle.LiveData;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.model.Project;

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
