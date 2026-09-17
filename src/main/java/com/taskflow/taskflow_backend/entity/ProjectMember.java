package com.taskflow.taskflow_backend.entity;

import com.taskflow.taskflow_backend.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "project_members",uniqueConstraints = {@UniqueConstraint(name = "uk_project_member",columnNames = {"project_id","user_id"})})
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "project_roles",joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "roles",nullable = false)
    private Set<ProjectRole> roles;

    @Column(name = "joined_at",nullable = false,updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate(){
        this.joinedAt = LocalDateTime.now();
    }
}
