package com.taskflow.taskflow_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.taskflow_backend.dto.request.comment.CreateCommentRequest;
import com.taskflow.taskflow_backend.dto.response.CommentResponse;
import com.taskflow.taskflow_backend.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/project/task/{taskId}/comment")
@RequiredArgsConstructor 
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(taskId, request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CommentResponse>> getAllComment(@PathVariable Long taskId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComment(taskId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long taskId, @PathVariable Long commentId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComment(taskId, commentId));
    }
}
