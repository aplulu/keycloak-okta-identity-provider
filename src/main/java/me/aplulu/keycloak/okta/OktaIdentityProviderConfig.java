package me.aplulu.keycloak.okta;

import org.keycloak.broker.oidc.OIDCIdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

public class OktaIdentityProviderConfig extends OIDCIdentityProviderConfig {

        public OktaIdentityProviderConfig(IdentityProviderModel model) {
            super(model);
        }

        public OktaIdentityProviderConfig() {
        }

        public String getOktaDomain() {
            return getConfig().get("oktaDomain");
        }

        public void setOktaDomain(String oktaDomain) {
            getConfig().put("oktaDomain", oktaDomain);
        }
}
