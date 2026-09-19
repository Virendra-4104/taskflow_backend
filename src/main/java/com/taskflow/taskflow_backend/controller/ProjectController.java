package com.taskflow.taskflow_backend.controller;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;
import com.taskflow.taskflow_backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PostMapping("/update/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody UpdateProjectRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(projectId, request));
    }

    @GetMapping("/get/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProject(projectId));
    }

    @GetMapping("/owned")
    public ResponseEntity<List<ProjectResponse>> getAllProjectByCreator(){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjectByCreator());
    }

    @GetMapping("/joined")
    public ResponseEntity<List<ProjectResponse>> getAllProjectByMember(){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjectByMember());
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<Void> permanentlyDeleteProject(@PathVariable Long projectId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
