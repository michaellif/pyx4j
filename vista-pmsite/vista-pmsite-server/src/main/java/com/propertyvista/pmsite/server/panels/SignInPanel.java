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

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.DemoData;

public class SignInPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(SignInPanel.class);

    /** True if the user should be remembered via form persistence (cookies) */
    private boolean rememberMe = true;

    private String password;

    private String username;

    public SignInPanel(final String id) {
        super(id);

        add(new FeedbackPanel("feedback"));

        add(new SignInForm("signInForm"));
    }

    @Override
    protected void onBeforeRender() {
        // logged in already?
        if (isSignedIn() == false) {
            // get username and password from persistence store
            String[] data = getApplication().getSecuritySettings().getAuthenticationStrategy().load();

            if ((data != null) && (data.length > 1)) {
                // try to sign in the user
                if (signIn(data[0], data[1])) {
                    username = data[0];
                    password = data[1];

                    // logon successful. Continue to the original destination
                    if (!continueToOriginalDestination()) {
                        // Ups, no original destination. Go to the home page
                        throw new RestartResponseException(getApplication().getSessionSettings().getPageFactory().newPage(getApplication().getHomePage()));
                    }
                }
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

    private boolean signIn(String username, String password) {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

    private boolean isSignedIn() {
        return AuthenticatedWebSession.get().isSignedIn();
    }

    public final class SignInForm extends StatelessForm<SignInPanel> {

        private static final long serialVersionUID = 1L;

        public SignInForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<SignInPanel>(SignInPanel.this));

            add(new TextField<String>("username"));
            add(new PasswordTextField("password"));

            add(new CheckBox("rememberMe"));
            add(new Button("signIn").add(AttributeModifier.replace("value", i18n.tr("Sign In"))));

            if (ApplicationMode.isDevelopment()) {
                Label devNotes = new Label("signInHint", "*)This application is running in <b>DEVELOPMENT</b> mode.<br/><i>Username and password are both \""
                        + DemoData.getDemoCustemerEmail(1) + "\"</i>");
                devNotes.setEscapeModelStrings(false);
                add(devNotes);
            }
        }

        @Override
        public final void onSubmit() {
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();

            if (signIn(getUsername(), getPassword())) {
                if (rememberMe == true) {
                    strategy.save(username, password);
                } else {
                    strategy.remove();
                }

                if (!continueToOriginalDestination()) {
                    setResponsePage(getApplication().getHomePage());
                }
            } else {
                strategy.remove();
                error("Sign in failed");
            }
        }
    }
}
