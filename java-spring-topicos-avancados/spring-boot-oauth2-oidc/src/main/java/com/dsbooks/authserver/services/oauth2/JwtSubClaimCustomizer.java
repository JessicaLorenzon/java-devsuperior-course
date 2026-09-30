package com.dsbooks.authserver.services.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import com.dsbooks.authserver.entities.UserEntity;

/**
 * Garante que o claim `sub` do access token e do ID token seja o UUID do usuário,
 * e não o email (que é o que o Spring Security usa por padrão como principal name).
 *
 * Isso evita que dados pessoais (email) trafeguem pelos tokens — eles ficam disponíveis
 * apenas via /userinfo, conforme os scopes aprovados pelo client.
 *
 * Não atua para fluxos sem usuário (ex.: Client Credentials), em que o principal é o
 * próprio RegisteredClient e o sub natural já é o client_id.
 */
@Component
public class JwtSubClaimCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication principal = context.getPrincipal();
        Object principalObj = principal.getPrincipal();
        if (principalObj instanceof UserEntity user) {
            context.getClaims().subject(user.getSub());
        }
    }
}
