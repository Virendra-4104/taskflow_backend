package com.taskflow.taskflow_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.Team;
import com.taskflow.taskflow_backend.entity.User;

import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(User createdBy);
    List<Project> findByCreatedByAndTeam(User createdBy, Team team);
}
