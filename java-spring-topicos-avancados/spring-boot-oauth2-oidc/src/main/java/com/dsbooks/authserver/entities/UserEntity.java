package com.dsbooks.authserver.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user")
public class UserEntity implements UserDetails {

    // ---- Campos internos do banco -----------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    // ---- Scope openid -----------------------------------------------------

    /** Subject identifier exposto nos tokens. UUID — não vaza dado pessoal. */
    @Column(unique = true, nullable = false)
    private String sub;

    // ---- Scope profile ----------------------------------------------------

    private String name;

    private String picture;

    // ---- Scope email ------------------------------------------------------

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    public UserEntity() {
    }

    public UserEntity(Long id, String sub, String email, String password,
                      String name, String picture, boolean emailVerified) {
        this.id = id;
        this.sub = sub;
        this.email = email;
        this.password = password;
        this.name = name;
        this.picture = picture;
        this.emailVerified = emailVerified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * O contrato de UserDetails exige um identificador. Usamos o email,
     * que é o que o usuário digita na tela de login.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
