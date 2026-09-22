package com.taskflow.taskflow_backend.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.taskflow.taskflow_backend.dto.request.comment.CreateCommentRequest;
import com.taskflow.taskflow_backend.dto.response.CommentResponse;
import com.taskflow.taskflow_backend.entity.Comment;
import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.Task;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import com.taskflow.taskflow_backend.exception.ResourceNotFoundException;
import com.taskflow.taskflow_backend.repository.CommentRepository;
import com.taskflow.taskflow_backend.repository.HistoryRepository;
import com.taskflow.taskflow_backend.repository.TaskRepository;
import com.taskflow.taskflow_backend.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final SecurityUtils securityUtils;
    private final TaskRepository taskRepository;

    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));
        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssignedUser = task.getAssignedTo().getId().equals(currentUser.getId());

        if (!isCreator && !isAssignedUser) {
            throw new AccessDeniedException("Not Authorized.");
        }
        Comment comment = Comment.builder()
                .content(request.content())
                .createdBy(currentUser)
                .task(task)
                .build();
        Comment saveComment = commentRepository.save(comment);
        History history = History.builder()
                .entityType(HistoryEntityType.COMMENT)
                .entityId(saveComment.getId())
                .action(HistoryAction.CREATED)
                .createdBy(currentUser)
                .build();
        historyRepository.save(history);
        return mapToCommentResponse(saveComment);
    }

    public List<CommentResponse> getAllComment(Long taskId) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));
        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssignedUser = task.getAssignedTo().getId().equals(currentUser.getId());

        if (!isCreator && !isAssignedUser) {
            throw new AccessDeniedException("Not Authorized.");
        }
        return commentRepository.findByTask(task)
                .stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    public CommentResponse getComment(Long taskId, Long commentId) {
        User currentUser = securityUtils.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found."));
        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssignedUser = task.getAssignedTo().getId().equals(currentUser.getId());

        if (!isCreator && !isAssignedUser) {
            throw new AccessDeniedException("Not Authorized.");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        return mapToCommentResponse(comment);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskId(comment.getTask().getId())
                .createdByUsername(comment.getCreatedBy().getUsername())
                .createdByEmail(comment.getCreatedBy().getEmail())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
