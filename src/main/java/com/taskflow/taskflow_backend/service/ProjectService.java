package com.taskflow.taskflow_backend.service;

import java.util.List;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;

public interface ProjectService {
    ProjectResponse createProject(CreateProjectRequest request);
    List<ProjectResponse> getAllProject();
    ProjectResponse getProjectById(Long projectId);
    ProjectResponse updateProject(Long longId, UpdateProjectRequest request);
    void deleteProject(Long projectId);
}
