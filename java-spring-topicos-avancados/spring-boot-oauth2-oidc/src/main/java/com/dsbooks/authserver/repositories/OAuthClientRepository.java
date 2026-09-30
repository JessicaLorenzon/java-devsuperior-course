package com.dsbooks.authserver.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsbooks.authserver.entities.OAuthClient;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

    Optional<OAuthClient> findByClientId(String clientId);
}
