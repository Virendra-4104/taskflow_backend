package com.taskflow.taskflow_backend.controller;

import com.taskflow.taskflow_backend.dto.request.project.CreateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project.UpdateProjectRequest;
import com.taskflow.taskflow_backend.dto.request.project_member.AddMemberRequest;
import com.taskflow.taskflow_backend.dto.request.project_member.RemoveMemberRequest;
import com.taskflow.taskflow_backend.dto.response.ProjectResponse;
import com.taskflow.taskflow_backend.dto.response.project_member.MemberResponse;
import com.taskflow.taskflow_backend.service.ProjectMemberService;
import com.taskflow.taskflow_backend.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    @PostMapping
    @Operation(summary = "Create a project", description = "Creates a new project for the current user.")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update a project", description = "Updates the title and description of a project.")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId,
            @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(projectId, request));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get a project", description = "Returns a project by its ID.")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProject(projectId));
    }

    @GetMapping("/owned")
    @Operation(summary = "Get all owned projects", description = "Returns all projects created by the current user.")
    public ResponseEntity<List<ProjectResponse>> getAllProjectByCreator() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjectByCreator());
    }

    @GetMapping("/joined")
    @Operation(summary = "Get all joined projects", description = "Returns all projects where the current user is a member.")
    public ResponseEntity<List<ProjectResponse>> getAllProjectByMember() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjectByMember());
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete a project", description = "Permanently deletes a project and its associated project data.")
    public ResponseEntity<Void> permanentlyDeleteProject(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Project member Methods
    @PostMapping("/add-member")
    @Operation(summary = "Add a project member", description = "Adds a user to the project with the specified project role.")
    public ResponseEntity<String> addMember(@Valid @RequestBody AddMemberRequest request) {
        projectMemberService.addMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Member Joined.");
    }

    @DeleteMapping("/remove-member")
    @Operation(summary = "Remove a project member", description = "Removes a user from the specified project.")
    public ResponseEntity<String> addMember(@Valid @RequestBody RemoveMemberRequest request) {
        projectMemberService.removeMember(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Member removed.");
    }

    @GetMapping("/{projectId}/get-all-members")
    @Operation(summary = "Get all project member", description = "Returns all members of the specified project.")
    public ResponseEntity<List<MemberResponse>> getAllProjectMember(@PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectMemberService.getAllProjectMembers(projectId));
    }
}
