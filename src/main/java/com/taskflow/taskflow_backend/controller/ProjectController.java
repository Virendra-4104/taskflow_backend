package com.taskflow.taskflow_backend.controller;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project_member.AddMemberRequest;
import com.taskflow.taskflow_backend.dto.request.project_member.RemoveMemberRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;
import com.taskflow.taskflow_backend.dto.response.project_member.MemberResponse;
import com.taskflow.taskflow_backend.service.ProjectMemberService;
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
    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody UpdateProjectRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(projectId, request));
    }

    @GetMapping("/{projectId}")
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

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> permanentlyDeleteProject(@PathVariable Long projectId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
//  Project member Methods
    @PostMapping("/add-member")
    public ResponseEntity<String> addMember(@Valid @RequestBody AddMemberRequest request){
        projectMemberService.addMember(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Member Joined.");
    }

    @DeleteMapping("/remove-member")
    public ResponseEntity<String> addMember(@Valid @RequestBody RemoveMemberRequest request){
        projectMemberService.removeMember(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Member removed.");
    }

    @GetMapping("/{projectId}/get-all-members")
    public ResponseEntity<List<MemberResponse>> getAllProjectMember(@PathVariable Long projectId){
        return ResponseEntity.status(HttpStatus.OK).body(projectMemberService.getAllProjectMembers(projectId));
    }
}
