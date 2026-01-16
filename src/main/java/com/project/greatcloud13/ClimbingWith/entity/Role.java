package com.project.greatcloud13.ClimbingWith.entity;

public enum Role {
    ADMIN,
    GYM_MANAGER,
    MEMBER;

    public String getAuthority(){
        return "ROLE_"+this.name();
    }
}
