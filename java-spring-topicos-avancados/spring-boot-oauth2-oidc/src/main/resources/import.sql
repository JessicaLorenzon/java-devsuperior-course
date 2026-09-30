-- First-party client: DSBooks App (aplicação Next.js de catálogo de livros)
INSERT INTO tb_oauth_client (id, client_id, client_secret, client_name, redirect_uris, post_logout_redirect_uris, scopes, authorization_grant_types, client_authentication_methods, access_token_ttl_seconds, refresh_token_ttl_seconds, require_consent)
VALUES ('00000000-0000-0000-0000-000000000001', 'my-client-id', '{noop}my-client-secret', 'DSBooks App', 'http://localhost:3000/api/auth/callback/dsbooks-as', 'http://localhost:3000/api/post-logout', 'openid,profile,email', 'authorization_code,refresh_token', 'client_secret_basic,client_secret_post', 60, 2592000, false);

-- First-party backend client: DSBooks Backend (Spring backend de catálogo de livros, machine-to-machine)
INSERT INTO tb_oauth_client (id, client_id, client_secret, client_name, redirect_uris, post_logout_redirect_uris, scopes, authorization_grant_types, client_authentication_methods, access_token_ttl_seconds, refresh_token_ttl_seconds, require_consent)
VALUES ('00000000-0000-0000-0000-000000000010', 'my-backend', '{noop}my-client-secret', 'DSBooks Backend Service', '', '', 'openid', 'client_credentials', 'client_secret_basic', 500, 3600, false);

-- Manual testing client (Postman / browser)
INSERT INTO tb_oauth_client (id, client_id, client_secret, client_name, redirect_uris, post_logout_redirect_uris, scopes, authorization_grant_types, client_authentication_methods, access_token_ttl_seconds, refresh_token_ttl_seconds, require_consent)
VALUES ('00000000-0000-0000-0000-000000000099', 'manual-client', '{noop}my-client-secret', 'Manual Test Client', 'https://oauth.pstmn.io/v1/callback', 'http://localhost:3000/api/post-logout', 'openid,profile,email', 'authorization_code,refresh_token', 'client_secret_basic,client_secret_post', 3600, 2592000, false);
