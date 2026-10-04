package com.fmarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    public String document;

    public LocalDate birthdate;

    @Column(nullable = false)
    public String profile;

    public String thumb;

    @Column(nullable = false)
    public String email;

    public Boolean verified = false;

    public Boolean highlighted;

    @Column(name = "cover_photo")
    public String coverPhoto;

    public String tiktok;

    public String instagram;

    public String description;

    @Column(name = "personal_name")
    public String personalName;

    @Column(nullable = false)
    public String password;

    @Column(name = "created_at", insertable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;
}
