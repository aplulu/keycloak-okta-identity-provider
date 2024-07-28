FROM quay.io/keycloak/keycloak:25.0.2

COPY --chown=keycloak:keycloak target/keycloak-okta-identity-provider-1.0-SNAPSHOT.jar /opt/keycloak/providers/
