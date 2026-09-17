package com.taskflow.taskflow_backend.entity;

import com.taskflow.taskflow_backend.enums.HistoryAction;
import com.taskflow.taskflow_backend.enums.HistoryEntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "histories",indexes = {@Index(name = "idx_history_entity",columnList = "entity_type, entity_id, created_at")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type",nullable = false)
    private HistoryEntityType entityType;

    @Column(name = "entity_id",nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action",nullable = false)
    private HistoryAction action;

    @Column(name = "old_value",columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value",columnDefinition = "TEXT")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id",nullable = false)
    private User createdBy;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
