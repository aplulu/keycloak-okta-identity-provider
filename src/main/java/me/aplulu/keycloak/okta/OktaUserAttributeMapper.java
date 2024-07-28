package me.aplulu.keycloak.okta;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

public class OktaUserAttributeMapper extends AbstractJsonUserAttributeMapper {
    private static final String[] cp = new String[] { OktaIdentityProviderFactory.PROVIDER_ID };

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "okta-user-attribute-mapper";
    }
}
