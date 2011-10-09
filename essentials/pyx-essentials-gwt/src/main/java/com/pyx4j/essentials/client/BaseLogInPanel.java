/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 13, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public abstract class BaseLogInPanel extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static String HTML5_KEY = BaseSiteDispatcher.instance().getAppId() + ".userid";

    private static final Logger log = LoggerFactory.getLogger(BaseLogInPanel.class);

    private static I18n i18n = I18n.get(BaseLogInPanel.class);

    private final CEntityForm<AuthenticationRequest> form;

    protected CCheckBox rememberID;

    protected CHyperlink forgotPassword;

    protected CHyperlink googleLogin;

    public BaseLogInPanel() {

        getElement().getStyle().setPadding(30, Unit.PX);
        getElement().getStyle().setPaddingRight(10, Unit.PX);

        EntityFormFactory<AuthenticationRequest> formFactory = new EntityFormFactory<AuthenticationRequest>(AuthenticationRequest.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {

                return new IObject[][] {

                { proto().email() },

                { proto().password() },

                { proto().rememberID() },

                { proto().captcha() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<AuthenticationRequest> form) {
                form.get(proto().captcha()).setVisible(false);
            }
        };
        form = formFactory.createForm();
        form.populate(null);

        forgotPassword = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                onLogInComplete();
                onForgotPasswordRequest();
            }
        });
        forgotPassword.setValue(i18n.tr("Did you forget your password?"));

        rememberID = (CCheckBox) form.get(form.proto().rememberID());

        if (Storage.isSupported()) {
            String userId = Storage.getLocalStorageIfSupported().getItem(BaseLogInPanel.HTML5_KEY);
            if (CommonsStringUtils.isStringSet(userId)) {
                form.get(form.proto().email()).setValue(userId);
                rememberID.setValue(true);
            }
        } else {
            rememberID.setVisible(false);
        }

        form.setAllignment(LabelAlignment.TOP);
        add(form.asWidget());

        add(forgotPassword.asWidget());

        googleLogin = new CHyperlink(new Command() {

            @Override
            public void execute() {
                onLogInComplete();
                onGoogleAccountsLogin();
            }
        });
        googleLogin.setValue(i18n.tr("or login using Google Accounts"));
        googleLogin.setVisible(false);

        add(googleLogin.asWidget());

    }

    public abstract void onLogInComplete();

    protected void onForgotPasswordRequest() {

    }

    protected void onGoogleAccountsLogin() {

    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Log In");
    }

    @Override
    protected void onLoad() {
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                // ignore errors here since this is warm up request
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    MessageDialog.warn(i18n.tr("Application is in read-only"),
                            i18n.tr("Application is in read-only due to short maintenance.\nPlease try again in one hour"));
                }
            }

        };
        RPCManager.executeBackground(AuthenticationServices.GetReadOnly.class, null, callback);
    }

    @Override
    public boolean onClickOk() {
        CCaptcha captcha = ((CCaptcha) form.get(form.proto().captcha()));
        if (captcha.isVisible()) {
            if (captcha.isValueEmpty()) {
                MessageDialog.warn(i18n.tr("Validation failed."), i18n.tr("Captcha code is required"));
                return false;
            }
            captcha.retrieveValue();
        }

        ValidationResults validationResults = form.get(form.proto().email()).getParent().getValidationResults();
        if (!validationResults.isValid()) {
            MessageDialog.warn(i18n.tr("Validation failed."), validationResults.getMessagesText(false));
            return false;
        }

        AsyncCallback<AuthenticationResponse> callback = new BlockingAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                form.get(form.proto().password()).setValue(null);
                log.debug("Login Failed", caught);
                // TODO handle all types of error including problems with Internet connection and site reload.
                // also caught.getMessage() can be null
                MessageDialog.error(i18n.tr("Login Failed"), caught.getMessage());
                if (caught instanceof ChallengeVerificationRequired) {
                    form.get(form.proto().captcha()).setVisible(true);
                    form.get(form.proto().captcha()).setMandatory(true);
                } else if (form.get(form.proto().captcha()).isVisible()) {
                    ((CCaptcha) form.get(form.proto().captcha())).createNewChallenge();
                }
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                form.get(form.proto().password()).setValue(null);
                form.get(form.proto().captcha()).setMandatory(false);
                ClientContext.authenticated(result);
                if (Storage.isSupported()) {
                    if (rememberID.getValue()) {
                        Storage.getLocalStorageIfSupported().setItem(BaseLogInPanel.HTML5_KEY, form.get(form.proto().email()).getValue());
                    } else {
                        Storage.getLocalStorageIfSupported().removeItem(BaseLogInPanel.HTML5_KEY);
                    }
                }
                onLogInComplete();
            }
        };
        RPCManager.execute(AuthenticationServices.Authenticate.class, form.getValue(), callback);
        return false;
    }

    protected CEntityForm<AuthenticationRequest> getForm() {
        return form;
    }

}