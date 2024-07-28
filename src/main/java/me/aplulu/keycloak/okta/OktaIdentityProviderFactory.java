package me.aplulu.keycloak.okta;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class OktaIdentityProviderFactory extends AbstractIdentityProviderFactory<OktaIdentityProvider> implements SocialIdentityProviderFactory<OktaIdentityProvider> {
    public static final String PROVIDER_ID = "okta";

    @Override
    public String getName() {
        return "Okta";
    }

    @Override
    public OktaIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new OktaIdentityProvider(session, new OktaIdentityProviderConfig(model));
    }

    @Override
    public OktaIdentityProviderConfig createConfig() {
        return new OktaIdentityProviderConfig();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property().name("oktaDomain").label("Okta Domain").type(ProviderConfigProperty.STRING_TYPE).helpText("Okta Domain").defaultValue("https://dev-123456.okta.com").required(true).add()
                .build();
    }
}
