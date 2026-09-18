package com.taskflow.taskflow_backend.repository;

import com.taskflow.taskflow_backend.entity.History;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(HistoryEntityType entityType, Long entityId);

    List<History> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    void deleteByEntityTypeAndEntityId(HistoryEntityType entityType, Long entityId);
}
