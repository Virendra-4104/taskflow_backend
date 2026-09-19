package com.taskflow.taskflow_backend.repository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.ProjectMember;
import com.taskflow.taskflow_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    boolean existsByProjectAndUser(Project project, User user);

    @Query("""
                SELECT p
                FROM Project p
                JOIN ProjectMember pm ON pm.project = p
                WHERE pm.user = :user
                AND p.isDeleted = false
            """)
    List<Project> findAllProjectsByMember(@Param("user") User user);
}
