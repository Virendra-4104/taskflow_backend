package com.taskflow.taskflow_backend.controller;

import com.taskflow.taskflow_backend.dto.request.personal_task.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.personal_task.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;
import com.taskflow.taskflow_backend.service.PersonalTaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal-task")
@RequiredArgsConstructor
@Tag(name = "Personal Tasks ", description = "Personal Task management end points")
public class PersonalTaskController {
    private final PersonalTaskService personalTaskService;

    @PostMapping
    @Operation(summary = "Create a personal task", description = "Creates a new personal task for the current user.")
    public ResponseEntity<PersonalTaskResponse> createPersonalTask(
            @Valid @RequestBody CreatePersonalTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalTaskService.createPersonalTask(request));
    }

    @PutMapping("{taskId}")
    @Operation(summary = "Update a personal task", description = "Updates the details of a personal task.")
    public ResponseEntity<PersonalTaskResponse> updatePersonalTask(@PathVariable Long taskId,
            @RequestBody UpdatePersonalTaskRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.updatePersonalTask(taskId, request));
    }

    @GetMapping("/get-all")
    @Operation(summary = "Get all personal tasks", description = "Returns all personal tasks belonging to the current user.")
    public ResponseEntity<List<PersonalTaskResponse>> getAllPersonalTask() {
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.getAllPersonalTasks());
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a personal task", description = "Returns a personal task by its ID.")
    public ResponseEntity<PersonalTaskResponse> getPersonalTask(@PathVariable Long taskId) {
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.getPersonalTask(taskId));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a personal task", description = "Permanently deletes the specified personal task.")
    public ResponseEntity<Void> deletePersonalTask(@PathVariable Long taskId) {
        personalTaskService.deletePersonalTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
