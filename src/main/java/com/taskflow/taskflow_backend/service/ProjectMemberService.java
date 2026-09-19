package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.project_member.AddMemberRequest;
import com.taskflow.taskflow_backend.dto.request.project_member.RemoveMemberRequest;
import com.taskflow.taskflow_backend.dto.response.project_member.MemberResponse;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.ProjectMember;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.taskflow.taskflow_backend.exception.UserAlreadyExistsException;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.ProjectMemberRepository;
import com.taskflow.taskflow_backend.repository.ProjectRepository;
import com.taskflow.taskflow_backend.repository.UserRepository;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public void addMember(AddMemberRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findByIdAndIsDeletedIsFalse(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getCreatedBy().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("You are not authorized to add member");
        }

        User member = userRepository.findByEmail(request.memberEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (projectMemberRepository.existsByProjectAndUser(project, member)) {
            throw new UserAlreadyExistsException("User is already a member of this project.");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .user(member)
                .roles(Set.of(request.role()))
                .build();

        projectMemberRepository.save(projectMember);

        History history = History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(project.getId())
                .action(HistoryAction.MEMBER_JOINED)
                .newValue(projectMember.getUser().getEmail())
                .createdBy(currentUser)
                .build();
        historyRepository.save(history);
    }

    @Transactional
    public void removeMember(RemoveMemberRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findByIdAndIsDeletedIsFalse(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getCreatedBy().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("You are not authorized to remove member");
        }

        User member = userRepository.findByEmail(request.memberEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        ProjectMember projectMember = projectMemberRepository.findByProjectAndUser(project, member)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found."));

        projectMemberRepository.delete(projectMember);

        History history = History.builder()
                .entityType(HistoryEntityType.PROJECT)
                .entityId(project.getId())
                .action(HistoryAction.MEMBER_REMOVED)
                .newValue(projectMember.getUser().getEmail())
                .createdBy(currentUser)
                .build();
        historyRepository.save(history);
    }

    public List<MemberResponse> getAllProjectMembers(Long projectId) {
        User currentUser = securityUtils.getCurrentUser();

        Project project = projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found."));

        if (!project.getCreatedBy().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("You are not authorized to remove member");
        }
        return projectMemberRepository.findByProject(project)
                .stream()
                .map(this::mapToMemberResponse)
                .toList();
    }

    private MemberResponse mapToMemberResponse(ProjectMember member) {
        User user = member.getUser();
        return MemberResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
