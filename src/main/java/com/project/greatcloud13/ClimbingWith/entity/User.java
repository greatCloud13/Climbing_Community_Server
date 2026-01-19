package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maneged_gym_id")
    private Gym gym;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nickname;

    @Builder
    public User(String username, String email, String password, String nickname){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = Role.MEMBER;
        this.nickname = nickname;
    }
}

