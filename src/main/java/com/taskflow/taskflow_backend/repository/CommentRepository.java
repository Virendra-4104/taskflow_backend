package com.taskflow.taskflow_backend.repository;

import com.taskflow.taskflow_backend.entity.Comment;
import com.taskflow.taskflow_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask(Task task);
}
