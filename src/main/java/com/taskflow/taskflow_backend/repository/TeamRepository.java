package com.taskflow.taskflow_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow_backend.entity.Team;
import com.taskflow.taskflow_backend.entity.User;

public interface TeamRepository extends JpaRepository<Team,Long> {
    List<Team> findByCreatedBy(User createdBy);
}
