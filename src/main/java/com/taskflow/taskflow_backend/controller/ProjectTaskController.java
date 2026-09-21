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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project/{projectId}/task")
@RequiredArgsConstructor
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    @PostMapping("/create")
    public ResponseEntity<ProjectTaskResponse> createProjectTask(@PathVariable Long projectId  ,@Valid @RequestBody CreateProjectTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectTaskService.createProjectTask(projectId, request));
    }

    @PutMapping("/{taskId}/update")
    public ResponseEntity<ProjectTaskResponse> updateProjectTask(@PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody UpdateProjectTaskRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectTaskService.updateProjectTask(projectId, taskId, request));
    }

    @PatchMapping("/{taskId}/update-status")
    public ResponseEntity<ProjectTaskResponse> updateProjectTaskStatusByAssignedUser(@PathVariable Long projectId,
            @PathVariable Long taskId,@Valid  @RequestBody UpdateProjectTaskStatusRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(projectTaskService.updateProjectTaskStatus(projectId, taskId, request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ProjectTaskResponse>> getAllProjectTask(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getAllProjectTasks(projectId));
    }

    @GetMapping("/{taskId}/get")
    public ResponseEntity<ProjectTaskResponse> getProjectTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getProjectTask(projectId, taskId));
    }

    @GetMapping("/get-by-assigned")
    public ResponseEntity<List<ProjectTaskResponse>> getAllProjectByAssignedUser(@PathVariable  Long projectId){
        return ResponseEntity.status(HttpStatus.OK).body(projectTaskService.getAllProjectByAssignedUser(projectId));
    }

    @DeleteMapping("/{taskId}/delete/")
    public ResponseEntity<Void> deleteProjectTask(@PathVariable Long projectId, @PathVariable Long taskId){
        projectTaskService.deleteProjectTask(projectId, taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
