package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @JoinColumn(name = "managed_gym_id")
    private Gym gym;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nickname;

    private boolean isActive;

    @Builder
    public User(String username, String email, String password, String nickname){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = Role.MEMBER;
        this.nickname = nickname;
        this.isActive = true;
    }

    public void checkPassword(String password, PasswordEncoder passwordEncoder){
        if(!passwordEncoder.matches(passwordEncoder.encode(password), this.password)){
            throw new AccessDeniedException("계정 정보가 올바르지 않습니다");
        }
    }

    public void deactivate(){

        this.isActive = false;
    }

    public void activate(String password){

        if(!this.password.equals(password)){
            throw new IllegalArgumentException("잘못된 계정 정보 입니다.");
        }
        this.isActive = true;
    }

    public void assignGymManager(Gym gym){
        this.gym = gym;
        this.role = Role.GYM_MANAGER;
    }

    public void unassignGym() {
        this.gym = null;
        this.role = Role.MEMBER;
    }

}

