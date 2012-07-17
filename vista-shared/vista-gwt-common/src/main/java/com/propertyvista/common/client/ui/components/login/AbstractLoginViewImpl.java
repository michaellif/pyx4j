/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.SystemWallMessage;

import com.propertyvista.domain.DemoData;

public abstract class AbstractLoginViewImpl extends FormFlexPanel implements LoginView {

    private LoginView.Presenter presenter;

    /**
     * This is the login form for user's credentials.
     */
    protected final LoginForm form;

    public AbstractLoginViewImpl(String caption) {
        form = new LoginForm(caption, new Command() {
            @Override
            public void execute() {
                submit();
            }
        }, new Command() {
            @Override
            public void execute() {
                resetPassword();
            }
        }, devLoginValues());

        form.initContent();
        createContent();
    }

    /**
     * Override this function to initialize the view components, and don't forget to add the {@link #form} :).
     */
    protected abstract void createContent();

    protected abstract List<DevLoginData> devLoginValues();

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.reset();
        form.populateNew();
        form.disableCaptcha();

        if (presenter.getUserId() != null) {
            form.get(form.proto().email()).setValue(presenter.getUserId());
            form.get(form.proto().rememberID()).setValue(true);
        }
    }

    @Override
    public void enableHumanVerification() {
        form.reEnableCaptcha();
    }

    @Override
    public void reset() {
        form.reset();
    }

    @Override
    public void setWallMessage(SystemWallMessage message) {
        form.setWallMessage(message);
    }

    private void submit() {
        if (!form.isValid()) {
            form.setUnconditionalValidationErrorRendering(true);
            throw new UserRuntimeException(form.getValidationResults().getMessagesText(true, false));
        }
        presenter.login(form.getValue());
    }

    private void resetPassword() {
        presenter.gotoResetPassword();
    }

    public static class DevLoginData {

        public final char shortcut;

        public final DemoData.UserType user;

        public DevLoginData(DemoData.UserType user, char shortcut) {
            this.user = user;
            this.shortcut = shortcut;
        }
    }
}
