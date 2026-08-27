package com.taskflow.taskflow_backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


import com.taskflow.taskflow_backend.enums.Gender;
import com.taskflow.taskflow_backend.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(min = 3)
    @Column(name = "username", length = 50, nullable = false)
    private String username;
    
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Size(min = 6)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Tasks 
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Task> createdTask;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    private List<Task> assignedTask;

    // projects
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Project> createdProjects;

    // comments
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Comment> createdComments;

    // Project membership
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<ProjectMember> projectMemberships;

    // histories
    @OneToMany(mappedBy = "createdBy",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<History> histories;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

}
