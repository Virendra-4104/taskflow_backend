package com.taskflow.taskflow_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow_backend.entity.Comment;
import com.taskflow.taskflow_backend.entity.Task;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByTask(Task task);
}
