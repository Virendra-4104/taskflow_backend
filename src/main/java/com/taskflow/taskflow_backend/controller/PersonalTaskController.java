package com.taskflow.taskflow_backend.controller;

import com.taskflow.taskflow_backend.dto.request.personal_task.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.personal_task.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;
import com.taskflow.taskflow_backend.service.PersonalTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal-task")
@RequiredArgsConstructor
public class PersonalTaskController {
    private final PersonalTaskService personalTaskService;

    @PostMapping
    public ResponseEntity<PersonalTaskResponse> createPersonalTask(@Valid @RequestBody CreatePersonalTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalTaskService.createPersonalTask(request));
    }

    @PutMapping("{taskId}")
    public ResponseEntity<PersonalTaskResponse> updatePersonalTask(@PathVariable Long taskId, @RequestBody UpdatePersonalTaskRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.updatePersonalTask(taskId, request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PersonalTaskResponse>> getAllPersonalTask() {
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.getAllPersonalTasks());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<PersonalTaskResponse> getPersonalTask(@PathVariable Long taskId){
        return ResponseEntity.status(HttpStatus.OK).body(personalTaskService.getPersonalTask(taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deletePersonalTask(@PathVariable Long taskId){
        personalTaskService.deletePersonalTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
