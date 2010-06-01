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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.webstorage.client.HTML5LocalStorage;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public abstract class BaseLogInPanel extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static String HTML5_KEY = BaseSiteDispatcher.instance().getAppId() + ".userid";

    private static final Logger log = LoggerFactory.getLogger(BaseLogInPanel.class);

    private final CEntityForm<AuthenticationRequest> form;

    protected CCheckBox rememberID;

    protected CHyperlink forgotPassword;

    public BaseLogInPanel() {

        getElement().getStyle().setPadding(30, Unit.PX);
        getElement().getStyle().setPaddingRight(10, Unit.PX);

        EntityFormFactory<AuthenticationRequest> formFactory = new EntityFormFactory<AuthenticationRequest>(AuthenticationRequest.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {

                return new IObject[][] {

                { meta().email() },

                { meta().password() },

                { meta().rememberID() },

                { meta().captcha() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<AuthenticationRequest> form) {
                form.get(meta().captcha()).setVisible(false);
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
        forgotPassword.setValue("Did you forget your password?");

        rememberID = (CCheckBox) form.get(form.meta().rememberID());

        if (HTML5LocalStorage.isSupported()) {
            String userId = HTML5LocalStorage.getLocalStorage().getItem(BaseLogInPanel.HTML5_KEY);
            if (CommonsStringUtils.isStringSet(userId)) {
                form.get(form.meta().email()).setValue(userId);
                rememberID.setValue(true);
            }
        } else {
            rememberID.setVisible(false);
        }

        form.setAllignment(LabelAlignment.TOP);
        add((Widget) form.initNativeComponent());

        add(forgotPassword.initNativeComponent());

    }

    public abstract void onLogInComplete();

    public abstract void onForgotPasswordRequest();

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public String optionTextOk() {
        return "Log In";
    }

    @Override
    public boolean onClickOk() {
        CCaptcha captcha = ((CCaptcha) form.get(form.meta().captcha()));
        if (captcha.isVisible()) {
            if (captcha.isValueEmpty()) {
                MessageDialog.warn("Validation failed.", "Captcha is required");
                return false;
            }
            captcha.retrieveValue();
        }

        ValidationResults validationResults = form.get(form.meta().email()).getParent().getValidationResults();
        if (!validationResults.isValid()) {
            MessageDialog.warn("Validation failed.", validationResults.getMessagesText());
            return false;
        }

        AsyncCallback<AuthenticationResponse> callback = new BlockingAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                log.debug("Login Failed", caught);
                // TODO handle all types of error including problems with Internet connection and site reload.
                // also caught.getMessage() can be null
                MessageDialog.error("Login Failed", caught.getMessage());
                if (caught instanceof ChallengeVerificationRequired) {
                    form.get(form.meta().captcha()).setVisible(true);
                }
                form.get(form.meta().captcha()).setValue(null);
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                if (HTML5LocalStorage.isSupported()) {
                    if (rememberID.getValue()) {
                        HTML5LocalStorage.getLocalStorage().setItem(BaseLogInPanel.HTML5_KEY, form.get(form.meta().email()).getValue());
                    } else {
                        HTML5LocalStorage.getLocalStorage().removeItem(BaseLogInPanel.HTML5_KEY);
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