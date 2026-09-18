package com.taskflow.taskflow_backend.repository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(User user);
}
