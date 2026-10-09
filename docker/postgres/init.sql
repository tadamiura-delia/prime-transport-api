-- Une base et un utilisateur dédiés par application.
CREATE USER app WITH PASSWORD 'app';
CREATE DATABASE app OWNER app;

CREATE USER keycloak WITH PASSWORD 'keycloak';
CREATE DATABASE keycloak OWNER keycloak;
