package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.project_task.CreateProjectTaskRequest;
import com.taskflow.taskflow_backend.dto.request.project_task.UpdateProjectTaskRequest;
import com.taskflow.taskflow_backend.dto.request.project_task.UpdateProjectTaskStatusRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectTaskResponse;
import com.taskflow.taskflow_backend.entity.*;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.taskflow.taskflow_backend.repository.*;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public ProjectTaskResponse createProjectTask(Long projectId, CreateProjectTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!currentUser.getId().equals(project.getCreatedBy().getId())) {
            throw new AccessDeniedException("Not Authorized.");
        }

        User user = userRepository.findByEmail(request.assignedToEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new UsernameNotFoundException("Member Not found in project."));

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .createdBy(currentUser)
                .project(project)
                .assignedTo(user)
                .isDeleted(false)
                .build();
        Task savedTask = taskRepository.save(task);

        History history = History.builder()
                .entityType(HistoryEntityType.TASK)
                .entityId(task.getId())
                .action(HistoryAction.CREATED)
                .createdBy(currentUser)
                .build();

        historyRepository.save(history);
        return mapToProjectTaskResponse(savedTask);
    }

    @Transactional
    public ProjectTaskResponse updateProjectTask(Long projectId, Long taskId, UpdateProjectTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())
                || !task.getProject().getId().equals(project.getId())) {
            throw new AccessDeniedException("Not Authorized");
        }

        List<History> histories = new ArrayList<>();

        Optional.ofNullable(request.title())
                .filter(title -> !title.isBlank())
                .ifPresent(newTitle -> {
                    if (!newTitle.equals(task.getTitle())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.TITLE_UPDATED)
                                .oldValue(task.getTitle())
                                .newValue(newTitle)
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setTitle(newTitle);
                });

        Optional.ofNullable(request.description())
                .filter(description -> !description.isBlank())
                .ifPresent(newDescription -> {
                    if (!newDescription.equals(task.getDescription())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.DESCRIPTION_UPDATED)
                                .oldValue(task.getDescription())
                                .newValue(newDescription)
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setDescription(newDescription);
                });

        Optional.ofNullable(request.status())
                .ifPresent(newStatus -> {
                    if (!newStatus.equals(task.getStatus())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.STATUS_UPDATED)
                                .oldValue(task.getStatus().toString())
                                .newValue(newStatus.toString())
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setStatus(newStatus);
                });

        Optional.ofNullable(request.priority())
                .ifPresent(newPriority -> {
                    if (!newPriority.equals(task.getPriority())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.PRIORITY_UPDATED)
                                .oldValue(task.getPriority().toString())
                                .newValue(newPriority.toString())
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setPriority(newPriority);
                });

        Optional.ofNullable(request.dueDate())
                .ifPresent(newDueDate -> {
                    if (!newDueDate.equals(task.getDueDate())) {
                        histories.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.DUE_DATE_UPDATED)
                                .oldValue(task.getDueDate().toString())
                                .newValue(newDueDate.toString())
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setDueDate(newDueDate);
                });

        if (!histories.isEmpty()) {
            historyRepository.saveAll(histories);
        }

        return mapToProjectTaskResponse(taskRepository.save(task));
    }

    public ProjectTaskResponse updateProjectTaskStatus(Long projectId, Long taskId,
            UpdateProjectTaskStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getId().equals(task.getProject().getId())) {
            throw new AccessDeniedException("Not Authorized");
        }
        boolean isCreator = currentUser.getId().equals(task.getCreatedBy().getId());
        boolean isAssignedMember = currentUser.getId().equals(task.getAssignedTo().getId());

        if (!isCreator && !isAssignedMember) {
            throw new AccessDeniedException("Not Authorized");
        }

        List<History> history = new ArrayList<>();

        Optional.ofNullable(request.status())
                .ifPresent(newStatus -> {
                    if (!newStatus.equals(task.getStatus())) {
                        history.add(History.builder()
                                .entityType(HistoryEntityType.TASK)
                                .entityId(task.getId())
                                .action(HistoryAction.STATUS_UPDATED)
                                .oldValue(request.status().toString())
                                .newValue(newStatus.toString())
                                .createdBy(currentUser)
                                .build());
                    }
                    task.setStatus(newStatus);
                });

        Task savedTask = taskRepository.save(task);

        if (!history.isEmpty()) {
            historyRepository.saveAll(history);
        }

        return mapToProjectTaskResponse(savedTask);
    }

    public List<ProjectTaskResponse> getAllProjectTasks(Long projectId) {
        User currentUser = securityUtils.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!currentUser.getId().equals(project.getCreatedBy().getId())) {
            throw new AccessDeniedException("Not Authorized");
        }

        return taskRepository.findByCreatedByAndProject(currentUser, project)
                .stream()
                .map(this::mapToProjectTaskResponse)
                .toList();
    }

    public ProjectTaskResponse getProjectTask(Long projectId, Long taskId) {
        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getId().equals(task.getProject().getId())) {
            throw new AccessDeniedException("Not Authorized");
        }

        boolean isCreator = currentUser.getId().equals(task.getCreatedBy().getId());
        boolean isAssignedMember = currentUser.getId().equals(task.getAssignedTo().getId());

        if (!isCreator && !isAssignedMember) {
            throw new AccessDeniedException("Not Authorized");
        }

        return mapToProjectTaskResponse(task);
    }

    public List<ProjectTaskResponse> getAllProjectByAssignedUser(Long projectId) {
        User currentUser = securityUtils.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));
        return taskRepository.findByAssignedToAndProject(currentUser, project)
                .stream()
                .map(this::mapToProjectTaskResponse)
                .toList();
    }

    @Transactional
    public void deleteProjectTask(Long projectId, Long taskId) {

        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())
                || !task.getProject().getId().equals(project.getId())) {
            throw new AccessDeniedException("Not Authorized");
        }

        History history = History.builder()
                .entityType(HistoryEntityType.TASK)
                .entityId(task.getId())
                .action(HistoryAction.DELETED)
                .createdBy(currentUser)
                .build();
        historyRepository.save(history);

        taskRepository.delete(task);
    }

    private ProjectTaskResponse mapToProjectTaskResponse(Task task) {
        return ProjectTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdByEmail(task.getCreatedBy().getEmail())
                .createdByUsername(task.getCreatedBy().getUsername())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignedToEmail(task.getAssignedTo().getEmail())
                .dueDate(task.getDueDate())
                .projectId(task.getProject().getId())
                .build();
    }
}
