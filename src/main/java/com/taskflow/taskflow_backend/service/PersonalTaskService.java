package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.personal_task.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.personal_task.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Task;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.TaskRepository;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalTaskService {
    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;
    private final HistoryRepository historyRepository;

    // Create personal task
    @Transactional
    public PersonalTaskResponse createPersonalTask(CreatePersonalTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date must not be in past.");
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .createdBy(currentUser)
                .dueDate(request.dueDate())
                .isDeleted(false)
                .build();

        Task savedTask = taskRepository.save(task);

        History history = History.builder()
                .entityType(HistoryEntityType.TASK)
                .entityId(savedTask.getId())
                .action(HistoryAction.CREATED)
                .createdBy(currentUser)
                .build();

        historyRepository.save(history);
        return mapToPersonalTaskResponse(savedTask);
    }

    // Update Personal Task
    @Transactional
    public PersonalTaskResponse updatePersonalTask(Long taskId, UpdatePersonalTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized");
        }

        List<History> histories = new ArrayList<>();

        // title update
        Optional.ofNullable(request.title())
                .filter(title -> !title.isBlank())
                .ifPresent((newTitle) -> {
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

        // description update
        Optional.ofNullable(request.description())
                .filter(description -> !description.isBlank())
                .ifPresent((newDescription) -> {
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

        // status update
        Optional.ofNullable(request.status())
                .ifPresent((newStatus) -> {
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

        // priority update
        Optional.ofNullable(request.priority())
                .ifPresent((newPriority) -> {
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

        // due date update
        Optional.ofNullable(request.dueDate())
                .ifPresent((newDueDate) -> {
                    if (newDueDate.isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("Date must not be in past.");
                    }

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

        return mapToPersonalTaskResponse(taskRepository.save(task));
    }

    public List<PersonalTaskResponse> getAllPersonalTasks() {
        User currentUser = securityUtils.getCurrentUser();
        return taskRepository.findByCreatedByAndProjectIsNull(currentUser)
                .stream()
                .map(this::mapToPersonalTaskResponse)
                .toList();
    }

    public PersonalTaskResponse getPersonalTask(Long taskId) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized");
        }

        return mapToPersonalTaskResponse(task);
    }

    @Transactional
    public void deletePersonalTask(Long taskId) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized");
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

    private PersonalTaskResponse mapToPersonalTaskResponse(Task task) {
        return PersonalTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdBy(task.getCreatedBy().getUsername())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
