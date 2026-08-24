package com.taskflow.taskflow_backend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.taskflow_backend.dto.request.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Task;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.TaskRepository;
import com.taskflow.taskflow_backend.service.TaskService;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;
    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    public PersonalTaskResponse createPersonalTask(CreatePersonalTaskRequest request) {
        User user = securityUtils.getCurrentUser();

        if(request.dueDate().isBefore(LocalDate.now())){
            throw new RuntimeException("Date must not be in past.");
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .createdBy(user)
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        History taskHistory = History.builder()
                .action(HistoryAction.CREATED)
                .createdBy(user)
                .entityType(HistoryEntityType.TASK)
                .entityId(savedTask.getId())
                .build();
        historyRepository.save(taskHistory);
        return mapToPersonalTaskResponse(savedTask);
    }

    @Override
    @Transactional
    public PersonalTaskResponse updatePersonalTask(Long taskId, UpdatePersonalTaskRequest request) {
        User user = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found."));

        if (!task.getCreatedBy().getId().equals(user.getId()))
            throw new RuntimeException("Task not found.");

        List<History> histories = new ArrayList<>();

        // title update
        Optional.ofNullable(request.title())
                .filter(t -> !t.isBlank())
                .ifPresent(newTitle -> {
                    if (!newTitle.equals(task.getTitle())) {
                        histories.add(buildHistory(HistoryEntityType.TASK, task.getId(), user,
                                HistoryAction.TITLE_UPDATED, task.getTitle(), newTitle));
                    }
                    task.setTitle(newTitle);
                });
        // description update
        Optional.ofNullable(request.description())
                .filter(d -> !d.isBlank())
                .ifPresent(newDescription -> {
                    if (!newDescription.equals(task.getDescription())) {
                        histories.add(buildHistory(HistoryEntityType.TASK, task.getId(), user,
                                HistoryAction.DESCRIPTION_UPDATED, task.getDescription(), newDescription));
                    }
                    task.setDescription(newDescription);
                });

        // status update
        Optional.ofNullable(request.status())
                .ifPresent(newStatus -> {
                    if (!newStatus.equals(task.getStatus())) {
                        histories.add(buildHistory(HistoryEntityType.TASK, task.getId(), user,
                                HistoryAction.STATUS_UPDATED, task.getStatus().toString(), newStatus.toString()));
                    }
                    task.setStatus(newStatus);
                });

        // priority update
        Optional.ofNullable(request.priority())
                .ifPresent(newPriority -> {
                    if (!newPriority.equals(task.getPriority())) {
                        histories.add(buildHistory(HistoryEntityType.TASK, task.getId(), user,
                                HistoryAction.PRIORITY_UPDATED, task.getPriority().toString(), newPriority.toString()));
                    }
                    task.setPriority(newPriority);
                });

        // dueDate update
        Optional.ofNullable(request.dueDate())
                .ifPresent(newDueDate -> {
                    if(newDueDate.isBefore(LocalDate.now())){
                        throw new RuntimeException("Date must not be in past.");
                    }
                    
                    if (!newDueDate.equals(task.getDueDate())) {
                        histories.add(buildHistory(HistoryEntityType.TASK, task.getId(), user,
                                HistoryAction.DUE_DATE_UPDATED, task.getDueDate().toString(), newDueDate.toString()));
                    }
                    task.setDueDate(newDueDate);
                });
        Task savedTask = taskRepository.save(task);

        if (!histories.isEmpty()) {
            historyRepository.saveAll(histories);
        }

        return mapToPersonalTaskResponse(savedTask);
    }

    @Override
    public PersonalTaskResponse getPersonalTaskById(Long taskId) {
        User user = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found."));
        if (!task.getCreatedBy().getId().equals(user.getId()))
            throw new RuntimeException("Task not found.");
        return mapToPersonalTaskResponse(task);
    }

    @Override
    public List<PersonalTaskResponse> getAllPersonalTask() {
        User user = securityUtils.getCurrentUser();
        return taskRepository.findByCreatedByAndProjectIsNull(user)
                .stream()
                .map(this::mapToPersonalTaskResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deletePersonalTaskById(Long taskId) {
        User user = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found."));
        
        if (!task.getCreatedBy().getId().equals(user.getId()))
            throw new RuntimeException("Task not found.");
        historyRepository.deleteByEntityTypeAndEntityId(HistoryEntityType.TASK, taskId);
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
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private History buildHistory(HistoryEntityType entityType, Long entityId, User changedBy, HistoryAction action,
            String oldValue, String newValue) {
        return History.builder()
                .entityType(entityType)
                .entityId(entityId)
                .createdBy(changedBy)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }

}
