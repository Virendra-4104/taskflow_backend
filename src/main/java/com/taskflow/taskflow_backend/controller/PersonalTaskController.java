package com.taskflow.taskflow_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.taskflow_backend.dto.request.personal_task.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.personal_task.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;
import com.taskflow.taskflow_backend.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/personal-task")
@RequiredArgsConstructor
public class PersonalTaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<PersonalTaskResponse> createPersonalTask(@Valid @RequestBody CreatePersonalTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createPersonalTask(request));
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<PersonalTaskResponse> updatePersonalTask(@PathVariable Long taskId, @RequestBody UpdatePersonalTaskRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updatePersonalTask(taskId, request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PersonalTaskResponse>> getAllPersonalTask(){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllPersonalTask());
    }

    @GetMapping("/get-by-id/{taskId}")
    public ResponseEntity<PersonalTaskResponse> getPersonalTaskById(@PathVariable Long taskId){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getPersonalTaskById(taskId));
    }

    @DeleteMapping("/delete-by-id/{taskId}")
    public ResponseEntity<Void> deletePersonalTask(@PathVariable Long taskId){
        taskService.deletePersonalTaskById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}