package com.taskflow.taskflow_backend.repository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedByAndIsDeletedIsFalse(User user);
    Optional<Project> findByIdAndIsDeletedIsFalse(Long projectId);
}
