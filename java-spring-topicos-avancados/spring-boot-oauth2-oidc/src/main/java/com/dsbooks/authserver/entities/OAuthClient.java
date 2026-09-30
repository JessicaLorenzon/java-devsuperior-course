package com.dsbooks.authserver.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_oauth_client")
public class OAuthClient {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String clientId;

    // Pode ser null para public clients (sem secret)
    private String clientSecret;

    @Column(nullable = false)
    private String clientName;

    /** Redirect URIs separadas por vírgula */
    @Column(nullable = false, length = 1000)
    private String redirectUris;

    /** Post-logout redirect URIs separadas por vírgula (null = nenhuma) */
    @Column(length = 1000)
    private String postLogoutRedirectUris;

    /** Scopes separados por vírgula — ex: "openid,profile,email" */
    @Column(nullable = false, length = 500)
    private String scopes;

    /** Grant types separados por vírgula — ex: "authorization_code,refresh_token" */
    @Column(nullable = false, length = 200)
    private String authorizationGrantTypes;

    /** Métodos de autenticação do client separados por vírgula */
    @Column(nullable = false, length = 200)
    private String clientAuthenticationMethods;

    /** Validade do access token em segundos (default: 1 hora) */
    @Column(nullable = false)
    private long accessTokenTtlSeconds = 3600L;

    /** Validade do refresh token em segundos (default: 30 dias) */
    @Column(nullable = false)
    private long refreshTokenTtlSeconds = 2592000L;

    /** Se exige tela de consent para o usuário. False para apps first-party. */
    @Column(nullable = false)
    private boolean requireConsent = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public String getPostLogoutRedirectUris() {
        return postLogoutRedirectUris;
    }

    public void setPostLogoutRedirectUris(String postLogoutRedirectUris) {
        this.postLogoutRedirectUris = postLogoutRedirectUris;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public void setAuthorizationGrantTypes(String authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
    }

    public String getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public void setClientAuthenticationMethods(String clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public boolean isRequireConsent() {
        return requireConsent;
    }

    public void setRequireConsent(boolean requireConsent) {
        this.requireConsent = requireConsent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OAuthClient other = (OAuthClient) obj;
        return Objects.equals(id, other.id);
    }
}
