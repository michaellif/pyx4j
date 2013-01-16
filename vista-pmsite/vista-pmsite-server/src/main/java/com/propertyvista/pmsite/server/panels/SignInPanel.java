/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 9, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.concurrent.atomic.AtomicBoolean;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;

import com.propertyvista.domain.DemoData;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.model.WicketUtils.JSActionLink;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;
import com.propertyvista.pmsite.server.pages.PwdResetPage;
import com.propertyvista.pmsite.server.pages.RegistrationPage;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.shared.config.VistaDemo;

public class SignInPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(SignInPanel.class);

    /** True if the user should be remembered via form persistence (cookies) */
    private boolean rememberMe = true;

    private String password;

    private String username;

    private final Pair<String, String> captchaResponse;

    private boolean captchaRequired = false;

    private SignInForm form;

    public SignInPanel(final String id) {
        super(id);

        add(new FeedbackPanel("feedback"));

        add(form = new SignInForm("signInForm"));

        IRequestParameters params = getRequest().getRequestParameters();
        captchaResponse = new Pair<String, String>(params.getParameterValue("recaptcha_challenge_field").toString(), params.getParameterValue(
                "recaptcha_response_field").toString());
    }

    @Override
    protected void onBeforeRender() {
        // logged in already?
        if (isSignedIn() == false) {
            // get username and password from persistence store
            String[] data = getApplication().getSecuritySettings().getAuthenticationStrategy().load();

            if ((data != null) && (data.length > 1)) {
                // try to sign in the user
                if (signIn(data[0], data[1], null)) {
                    username = data[0];
                    password = data[1];

                    // logon successful. Continue to the original destination
                    if (!continueToOriginalDestination()) {
                        // Ups, no original destination. Go to the home page
                        throw new RestartResponseException(getApplication().getSessionSettings().getPageFactory().newPage(getApplication().getHomePage()));
                    }
                }
            }
            // Captcha may be required after form.onSubmit() returns
            if (useCaptcha()) {
                form.addCaptcha();
            }
        }

        // don't forget
        super.onBeforeRender();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public boolean useCaptcha() {
        return captchaRequired;
    }

    private boolean signIn(final String username, final String password, Pair<String, String> captcha) {
        AuthenticationRequest request = EntityFactory.create(AuthenticationRequest.class);
        request.email().setValue(username);
        request.password().setValue(password);
        request.captcha().setValue(captcha);

        final AtomicBoolean rc = new AtomicBoolean(false);
        // This does the actual authentication; will throw an exception in case of failure
        LocalService.create(PortalAuthenticationService.class).authenticate(new AsyncCallback<AuthenticationResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    captchaRequired = true;
                } else if (caught instanceof UserRuntimeException) {
                    error(caught.getMessage());
                } else if (ApplicationMode.isDevelopment()) {
                    error(caught.getMessage());
                } else {
                    error(i18n.tr("Action failed. Please try again later."));
                }
                rc.set(false);
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                // Our wicket session authentication simply returns true, so this call will just create wicket session
                rc.set(AuthenticatedWebSession.get().signIn(username, password));
            }
        }, new ClientSystemInfo(), request);

        return rc.get();
    }

    private boolean isSignedIn() {
        return AuthenticatedWebSession.get().isSignedIn();
    }

    public final class SignInForm extends StatelessForm<SignInPanel> {

        private static final long serialVersionUID = 1L;

        private final Label captcha = new Label("captcha", "");

        public SignInForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<SignInPanel>(SignInPanel.this));

            add(new TextField<String>("username"));
            add(new PasswordTextField("password"));

            // captcha
            add(captcha.setVisible(false));

            add(new CheckBox("rememberMe"));
            add(new PageLink("pwdreset", PwdResetPage.class).setText(i18n.tr("Forgot Password")));
            add(new PageLink("registration", RegistrationPage.class).setText(i18n.tr("Registration")));

            add(new Button("signIn").add(AttributeModifier.replace("value", i18n.tr("Sign In"))));

            if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
                Label devNotes = new Label("signInHint", "This application is running in <b>" + DemoData.applicationModeName()
                        + "</b> mode.<br/><i>Username and password are both<br/>");
                add(devNotes.setEscapeModelStrings(false));
                StringBuilder js = new StringBuilder();
                js.append("var f=document.getElementById('signInForm');");
                js.append("dev_selectAndSetNext(f.username,f.password,");
                js.append("new Array(");
                for (int n = 1; n < DemoData.UserType.TENANT.getDefaultMax(); n++) {
                    if (n != 1) {
                        js.append(",");
                    }
                    js.append("\"").append(DemoData.UserType.TENANT.getEmail(n)).append("\"");
                }
                js.append("))");
                add(new JSActionLink("signInLogin1", js.toString(), false).setBody(new Model<String>(DemoData.UserType.TENANT.getEmail(1))));
            }
        }

        public void addCaptcha() {
            String pubKey = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey();
            ReCaptcha c = ReCaptchaFactory.newReCaptcha(pubKey, "", false);
            captcha.setDefaultModelObject(c.createRecaptchaHtml(null, null)).setEscapeModelStrings(false).setVisible(true);
        }

        @Override
        public final void onSubmit() {
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();

            if (signIn(getUsername(), getPassword(), captchaResponse)) {
                if (rememberMe == true) {
                    strategy.save(username, password);
                } else {
                    strategy.remove();
                }
                // redirect to target url
                PMSiteApplication.get().redirectToTarget();
            } else {
                strategy.remove();
            }
        }
    }
}
