package me.aplulu.keycloak.okta;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.core.Response;
import org.keycloak.broker.oidc.OIDCIdentityProvider;
import org.keycloak.broker.oidc.OIDCIdentityProviderConfig;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.JsonWebToken;

import java.io.IOException;
import java.util.logging.Logger;

public class OktaIdentityProvider extends OIDCIdentityProvider implements SocialIdentityProvider<OIDCIdentityProviderConfig> {
    private static final Logger logger = Logger.getLogger("OktaIdentityProvider");
    private final OktaIdentityProviderConfig config;

    public static final String DEFAULT_SCOPE = "openid profile okta.users.read.self";

    public OktaIdentityProvider(KeycloakSession session, OktaIdentityProviderConfig config) {
        super(session, config);

        this.config = config;

        logger.info("OktaIdentityProvider created");
        logger.info("okta domain: " + config.getOktaDomain());

        config.setAuthorizationUrl(config.getOktaDomain() + "/oauth2/v1/authorize");
        config.setTokenUrl(config.getOktaDomain() + "/oauth2/v1/token");
        config.setUserInfoUrl(getProfileUrl());
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return getProfileUrl();
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }

    @Override
    protected BrokeredIdentityContext extractIdentity(AccessTokenResponse tokenResponse, String accessToken, JsonWebToken idToken) throws IOException {
        BrokeredIdentityContext identityContext = super.extractIdentity(tokenResponse, accessToken, idToken);

        fetchProfileFromAPI(accessToken, identityContext);

        logger.info("identityContext: " + identityContext.toString());
        identityContext.getAttributes().forEach((k, v) -> {
            logger.info("identityContextAttributes: " + k+"="+ v);
        });

        return identityContext;
    }

    private void fetchProfileFromAPI(String accessToken, BrokeredIdentityContext identityContext) {
        try (SimpleHttp.Response resp = SimpleHttp.doGet(getProfileUrl(), session)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .asResponse()) {

            if (Response.Status.fromStatusCode(resp.getStatus()).getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new RuntimeException("Failed to call external API: HTTP error code : " + resp.getStatus());
            }

            JsonNode node = resp.asJson();

            logger.info("userNode: " + node.toString());

            if (node.has("profile")) {
                JsonNode profile = node.get("profile");
                if (profile.isObject()) {
                    profile.fields().forEachRemaining(entry -> {
                        String key = entry.getKey();
                        JsonNode value = entry.getValue();
                        identityContext.setUserAttribute(key, value.asText());
                    });
                }
            }
        } catch (Exception e) {
            logger.severe("Exception: " + e.getMessage());
            throw new IdentityBrokerException("Could not obtain user profile from Okta", e);
        }
    }

    private String getProfileUrl() {
        return config.getOktaDomain() + "/api/v1/users/me";
    }
}
