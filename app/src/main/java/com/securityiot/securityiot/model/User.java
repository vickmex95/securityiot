package com.securityiot.securityiot.model;

public class User {

    private String userId;

    private String email;

    private String rol;

    private Boolean status;


    public User(String userId, String email, String password, String rol, Boolean status) {
        this.userId = userId;
        this.email = email;
        this.rol = rol;
        this.status = status;
    }

    public User(String userId, String email, String rol, Boolean status) {
        this.userId = userId;
        this.email = email;
        this.rol = rol;
        this.status = status;
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public User(){

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}