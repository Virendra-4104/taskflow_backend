package com.taskflow.taskflow_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.ProjectMember;
import com.taskflow.taskflow_backend.entity.User;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);

    Optional<ProjectMember> findByMember(User member);

    boolean existsByProjectAndMember(Project project, User user);
}
