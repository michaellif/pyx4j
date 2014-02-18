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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.DemoData;

public abstract class AbstractLoginViewImpl extends TwoColumnFlexFormPanel implements LoginView {

    private static final I18n i18n = I18n.get(AbstractLoginViewImpl.class);

    private LoginView.Presenter presenter;

    /**
     * This is the login form for user's credentials.
     */
    protected final LoginForm form;

    public AbstractLoginViewImpl(String caption) {
        setWidth("100%");
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
    }

    @Override
    public void enableHumanVerification() {
        form.reEnableCaptcha();
    }

    @Override
    public void reset(String userId, boolean rememberUser) {
        form.reset();
        form.populateNew();
        form.get(form.proto().email()).setValue(userId, true, true);
        form.get(form.proto().rememberID()).setValue(rememberUser, true, true);
        form.disableCaptcha();
    }

    @Override
    public void setWallMessage(SystemWallMessage message) {
        form.setWallMessage(message);
    }

    private void submit() {
        form.setVisited(true);
        if (!form.isValid()) {
            showValidationDialog();
        } else {
            presenter.login(form.getValue());
        }
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

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), form.getValidationResults().getValidationMessage(true));
    }
}
