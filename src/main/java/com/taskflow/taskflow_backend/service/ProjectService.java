package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.ProjectMember;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.enums.ProjectRole;
import com.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.ProjectMemberRepository;
import com.taskflow.taskflow_backend.repository.ProjectRepository;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;
    private final SecurityUtils securityUtils;

    //    Create project
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = securityUtils.getCurrent();
        Project project = Project.builder()
                .title(request.title())
                .description(request.description())
                .createdBy(currentUser)
                .isDeleted(false)
                .build();
        Project saveProject = projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .project(saveProject)
                .user(currentUser)
                .roles(Set.of(ProjectRole.PROJECT_ADMIN))
                .build();
        projectMemberRepository.save(member);

        List<History> histories = new ArrayList<>();
        histories.add(History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(saveProject.getId())
                .createdBy(currentUser)
                .action(HistoryAction.CREATED)
                .build());

        histories.add(History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(saveProject.getId())
                .createdBy(currentUser)
                .action(HistoryAction.MEMBER_JOINED)
                .build());

        historyRepository.saveAll(histories);
        return mapToProjectResponse(saveProject);
    }

    //    Update project
    @Transactional
    public ProjectResponse updateProject(Long projectId, UpdateProjectRequest request) {
        User currentUser = securityUtils.getCurrent();
        Project project = projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project Not found."));
        if (!project.getCreatedBy().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("You are not authorized to view this project");
        }
        List<History> histories = new ArrayList<>();
        Optional.ofNullable(request.title()).filter(title -> !title.isBlank()).ifPresent((newTitle) -> {
            if (!newTitle.equals(project.getTitle())) {
                histories.add(History.builder()
                        .entityType(HistoryEntityType.PROJECT)
                        .id(project.getId())
                        .createdBy(currentUser)
                        .action(HistoryAction.TITLE_UPDATED)
                        .oldValue(project.getTitle())
                        .newValue(newTitle)
                        .build());
            }
            project.setTitle(newTitle);
        });

        Optional.ofNullable(request.description())
                .filter(descriptio -> !descriptio.isBlank())
                .ifPresent((newDescription) -> {
                    if (!newDescription.equals(project.getDescription())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.PROJECT)
                                .entityId(project.getId())
                                .createdBy(currentUser)
                                .action(HistoryAction.DESCRIPTION_UPDATED)
                                .oldValue(project.getDescription())
                                .newValue(newDescription)
                                .build());
                    }
                    project.setDescription(newDescription);
                });

        Project savedProject = projectRepository.save(project);
        if (!histories.isEmpty()) {
            historyRepository.saveAll(histories);
        }
        return mapToProjectResponse(savedProject);
    }

    //    Get project by (only for creator and member)
    public ProjectResponse getProject(Long projectId) {
        User currentUser = securityUtils.getCurrent();
        Project project = projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isCreator = project.getCreatedBy().getEmail().equals(currentUser.getEmail());
        boolean isMember = projectMemberRepository.existsByProjectAndUser(project, currentUser);

        if (!isCreator && !isMember) {
            throw new AccessDeniedException("You are not authorized to view this project.");
        }

        return mapToProjectResponse(project);
    }

    //    Get all project by creator
    public List<ProjectResponse> getAllProjectByCreator() {
        User currentUser = securityUtils.getCurrent();
        return projectRepository.findByCreatedByAndIsDeletedIsFalse(currentUser)
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
    }

    //    Get project by member (or say joined member)
    public List<ProjectResponse> getAllProjectByMember() {
        User currentUser = securityUtils.getCurrent();
        return projectMemberRepository.findAllProjectsByMember(currentUser)
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
    }

//    Delete project permanently
    @Transactional
    public void permanentlyDeleteProject(Long projectId) {
        User currentUser = securityUtils.getCurrent();
        Project project = projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getCreatedBy().getEmail().equals(currentUser.getEmail())){
            throw new AccessDeniedException("You are not authorized to delete this project.");
        }

        History history = History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(project.getId())
                .createdBy(currentUser)
                .action(HistoryAction.DELETED)
                .build();

        historyRepository.save(history);
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
}
