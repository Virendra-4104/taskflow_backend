package com.taskflow.taskflow_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.taskflow_backend.dto.request.project_task.CreateProjectTaskRequest;
import com.taskflow.taskflow_backend.dto.request.project_task.UpdateProjectTaskRequest;
import com.taskflow.taskflow_backend.dto.request.project_task.UpdateProjectTaskStatusRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectTaskResponse;
import com.taskflow.taskflow_backend.service.ProjectTaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project/{projectId}/task")
@RequiredArgsConstructor
@Tag(name = "Project Tasks", description = "Project Task management endpoints")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    @PostMapping
    @Operation(summary = "Create a project Task", description = "Creates a new task in the specified project.")
    public ResponseEntity<ProjectTaskResponse> createProjectTask(@PathVariable Long projectId,
            @Valid @RequestBody CreateProjectTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectTaskService.createProjectTask(projectId, request));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update a project task", description = "Updates the details of a project task.")
    public ResponseEntity<ProjectTaskResponse> updateProjectTask(@PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody UpdateProjectTaskRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectTaskService.updateProjectTask(projectId, taskId, request));
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Update task status", description = "Updates the task status. This operation is available to the assigned user or project owner.")
    public ResponseEntity<ProjectTaskResponse> updateProjectTaskStatusByAssignedUser(@PathVariable Long projectId,
            @PathVariable Long taskId, @Valid @RequestBody UpdateProjectTaskStatusRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectTaskService.updateProjectTaskStatus(projectId, taskId, request));
    }

    @GetMapping("/get-all")
    @Operation(summary = "Get all project tasks", description = "Returns all tasks belonging to the specified project. Only the project owner can access this endpoint.")
    public ResponseEntity<List<ProjectTaskResponse>> getAllProjectTask(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getAllProjectTasks(projectId));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a project task", description = "Returns a project task by its ID.")
    public ResponseEntity<ProjectTaskResponse> getProjectTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getProjectTask(projectId, taskId));
    }

    @GetMapping("/assigned")
    @Operation(summary = "Get tasks assigned to the current user", description = "Returns all tasks in the specified project that are assigned to the current user.")
    public ResponseEntity<List<ProjectTaskResponse>> getAllProjectByAssignedUser(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getAllProjectByAssignedUser(projectId));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a project task", description = "Permanently deletes the specified project task.")
    public ResponseEntity<Void> deleteProjectTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        projectTaskService.deleteProjectTask(projectId, taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
