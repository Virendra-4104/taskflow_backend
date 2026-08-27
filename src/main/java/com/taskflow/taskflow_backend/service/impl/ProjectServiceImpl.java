package com.taskflow.taskflow_backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.ProjectMember;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.enums.Role;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.ProjectMemberRepository;
import com.taskflow.taskflow_backend.repository.ProjectRepository;
import com.taskflow.taskflow_backend.service.ProjectService;
import com.taskflow.taskflow_backend.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User user = securityUtils.getCurrentUser();
        Project project = Project.builder()
                .title(request.title())
                .description(request.description())
                .createdBy(user)
                .build();
        Project savedProject = projectRepository.save(project);
        ProjectMember member = ProjectMember.builder()
                .member(user)
                .project(project)
                .roles(Set.of(Role.PROJECT_ADMIN))
                .build();
        projectMemberRepository.save(member);

        History history = History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(project.getId())
                .action(HistoryAction.CREATED)
                .createdBy(user)
                .build();

        historyRepository.save(history);
        return mapToProjectResponse(savedProject);
    }

    @Override
    public List<ProjectResponse> getAllProject() {
        User user = securityUtils.getCurrentUser();
        return projectRepository.findByCreatedBy(user)
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
    }

    @Override
    public ProjectResponse getProjectById(Long projectId) {
        User user = securityUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found."));

        boolean isCreator = project.getCreatedBy().getId().equals(user.getId());
        boolean isMember = projectMemberRepository.existsByProjectAndMember(project, user);

        if (!isCreator && !isMember) {
            throw new RuntimeException("You don't have access to this project.");
        }
        return mapToProjectResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long projectId, UpdateProjectRequest request) {
        User user = securityUtils.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found."));
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You are not the owner.");
        }

        List<History> histories = new ArrayList<>();

        // title update
        Optional.ofNullable(request.title())
                .filter(t -> !t.isBlank())
                .ifPresent(newTitle -> {
                    if (!newTitle.equals(project.getTitle())) {
                        histories.add(buildHistory(HistoryEntityType.PROJECT, projectId, HistoryAction.TITLE_UPDATED,
                                user, newTitle, project.getTitle()));
                    }
                    project.setTitle(newTitle);
                });

        // description update
        Optional.ofNullable(request.description())
                .filter(d -> !d.isBlank())
                .ifPresent(newDescription -> {
                    if (!newDescription.equals(project.getDescription())) {
                        histories.add(buildHistory(HistoryEntityType.PROJECT, projectId,
                                HistoryAction.DESCRIPTION_UPDATED, user, newDescription, project.getDescription()));
                    }
                    project.setDescription(newDescription);
                });

        if (!histories.isEmpty()) {
            historyRepository.saveAll(histories);
        }

        return mapToProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        User user = securityUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found."));

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You are not the owner.");
        }
        historyRepository.deleteByEntityTypeAndEntityId(HistoryEntityType.PROJECT, projectId);
        projectRepository.delete(project);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdBy(project.getCreatedBy().getUsername())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private History buildHistory(HistoryEntityType entityType, Long entityId, HistoryAction action, User createdBy,
            String newValue, String oldValue) {
        return History.builder()
                .entityType(entityType)
                .entityId(entityId)
                .createdBy(createdBy)
                .action(action)
                .newValue(newValue)
                .oldValue(oldValue)
                .build();
    }

}
