package org.talend.components.magentocms.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DataStore("MagentoDataStore")
@Checkable("datastoreHealthcheck")
@GridLayout({ @GridLayout.Row({ "magentoWebServerUrl" }), @GridLayout.Row({ "authenticationType" }),
        @GridLayout.Row({ "authenticationOauth1Settings" }), @GridLayout.Row({ "authenticationTokenSettings" }),
        @GridLayout.Row({ "authenticationLoginPasswordSettings" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row({ "magentoRestVersion" }) })
@Documentation("Data store settings. Magento's server connection and authentication preferences")
public class MagentoDataStore implements Serializable {

    @Option
    @Documentation("URL of web server (including port after ':'), e.g. 'http://mymagentoserver.com:1234'")
    private String magentoWebServerUrl;

    @Option
    @Documentation("The version of Magento REST ,e.g. 'V1'")
    private RestVersion magentoRestVersion = RestVersion.V1;

    @Option
    @Documentation("authentication type (OAuth 1.0, Token, Login etc.)")
    private AuthenticationType authenticationType = AuthenticationType.LOGIN_PASSWORD;

    @Option
    @Documentation("authentication OAuth 1.0 settings")
    @ActiveIf(target = "authenticationType", value = { "OAUTH_1" })
    private AuthenticationOauth1Configuration authenticationOauth1Settings;

    @Option
    @Documentation("authentication Token settings")
    @ActiveIf(target = "authenticationType", value = { "AUTHENTICATION_TOKEN" })
    private AuthenticationTokenConfiguration authenticationTokenSettings;

    @Option
    @Documentation("authentication Login settings")
    @ActiveIf(target = "authenticationType", value = { "LOGIN_PASSWORD" })
    private AuthenticationLoginPasswordConfiguration authenticationLoginPasswordSettings;

    public AuthenticationConfiguration getAuthSettings() throws UnknownAuthenticationTypeException {
        if (authenticationType == AuthenticationType.OAUTH_1) {
            return authenticationOauth1Settings;
        } else if (authenticationType == AuthenticationType.AUTHENTICATION_TOKEN) {
            return authenticationTokenSettings;
        } else if (authenticationType == AuthenticationType.LOGIN_PASSWORD) {
            return authenticationLoginPasswordSettings;
        }
        throw new UnknownAuthenticationTypeException();
    }

    public String getMagentoBaseUrl() {
        return "index.php/rest/" + magentoRestVersion;
    }
}