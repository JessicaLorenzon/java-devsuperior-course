package com.dsbooks.authserver.services.oauth2;

import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.stereotype.Component;

import com.dsbooks.authserver.entities.UserEntity;
import com.dsbooks.authserver.repositories.UserRepository;

/**
 * Constrói a resposta do endpoint /userinfo com base nos scopes aprovados pelo client.
 *
 * - sub: sempre o UUID do usuário (mesmo que o `sub` do token).
 * - profile: name, picture.
 * - email: email, email_verified.
 *
 * Centraliza aqui (em vez de despejar tudo no ID token) a regra de exposição
 * de dados pessoais, mantendo os tokens enxutos.
 */
@Component
public class CustomOidcUserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        OAuth2Authorization authorization = context.getAuthorization();
        Set<String> scopes = context.getAccessToken().getScopes();
        String email = authorization.getPrincipalName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
                                "User no longer exists; token is stale", null)));

        OidcUserInfo.Builder builder = OidcUserInfo.builder()
                .subject(user.getSub());

        if (scopes.contains(OidcScopes.PROFILE)) {
            if (user.getName() != null) builder.name(user.getName());
            if (user.getPicture() != null) builder.picture(user.getPicture());
        }
        if (scopes.contains(OidcScopes.EMAIL)) {
            builder.email(user.getEmail());
            builder.emailVerified(user.isEmailVerified());
        }

        return builder.build();
    }
}
