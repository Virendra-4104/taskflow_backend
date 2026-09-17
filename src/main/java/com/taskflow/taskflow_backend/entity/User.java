package com.taskflow.taskflow_backend.entity;

import com.taskflow.taskflow_backend.enums.Gender;
import com.taskflow.taskflow_backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Size(min = 6)
    @Column(name = "password", nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "profile_img_url",length = 255)
    private String profileImgUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "assignedTo",fetch = FetchType.LAZY)
    private List<Task> assignedTask;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<ProjectMember> projectMemberShips;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<History> histories;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
