package com.taskflow.taskflow_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow_backend.entity.Project;
import com.taskflow.taskflow_backend.entity.Task;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Personal tasks
    List<Task> findByCreatedByAndProjectIsNull(User createdBy);

    List<Task> findByCreatedByAndStatusAndProjectIsNull(User createdBy, Status status);

    List<Task> findByCreatedByAndPriorityAndProjectIsNull(User createdBy, Priority priority);

    // Project tasks
    List<Task> findByProject(Project project);

    List<Task> findByCreatedByAndProject(User createdBy, Project project);

    List<Task> findByAssignedToAndProject(User assignedTo, Project project);

    List<Task> findByAssignedTo(User assignedTo);

    List<Task> findByProjectAndStatus(Project project, Status status);

    List<Task> findByProjectAndPriority(Project project, Priority priority);


}
