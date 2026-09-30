package com.dsbooks.authserver.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.dsbooks.authserver.services.oauth2.CustomOidcUserInfoMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    @Value("${app.security.issuer}")
    private String issuer;

    @Autowired
    private CustomOidcUserInfoMapper userInfoMapper;

    // ---------------------------------------------------------
    // Filter chain 1: endpoints do Authorization Server
    // (ex: /oauth2/authorize, /oauth2/token, /userinfo, etc.)
    // ---------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, as -> as
                // Habilita OpenID Connect (ID token, /userinfo, discovery) e instala
                // o mapper customizado de /userinfo, que devolve email/name/picture
                // conforme os scopes aprovados (sem despejar esses claims no ID token).
                .oidc(oidc -> oidc
                    .userInfoEndpoint(userInfo -> userInfo
                        .userInfoMapper(userInfoMapper)
                    )
                )
            )
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            // Redireciona para /login quando o browser acessa um endpoint protegido
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            );

        return http.build();
    }

    // ---------------------------------------------------------
    // Filter chain 2: demais rotas (tela de login)
    // ---------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // H2 console é um servlet separado (fora do DispatcherServlet), então o
        // requestMatchers(String) padrão não casa. Usar PathPatternRequestMatcher.
        PathPatternRequestMatcher h2Console = PathPatternRequestMatcher.pathPattern("/h2-console/**");

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .requestMatchers(h2Console).permitAll()
                .anyRequest().authenticated()
            )
            // H2 console envia POST sem CSRF token e usa <iframe>: precisa ignorar CSRF
            // e permitir frames de mesma origem nessas rotas.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(h2Console)
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // ---------------------------------------------------------
    // Par de chaves RSA para assinar os JWTs
    // (gerado em memória — será externalizado em produção)
    // ---------------------------------------------------------
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    // Necessário para que o AS possa decodificar seus próprios tokens
    // (usado internamente pelo endpoint /userinfo e por introspection)
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // ---------------------------------------------------------
    // Configurações gerais do Authorization Server
    // ---------------------------------------------------------
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer(issuer)
            .build();
    }
}
