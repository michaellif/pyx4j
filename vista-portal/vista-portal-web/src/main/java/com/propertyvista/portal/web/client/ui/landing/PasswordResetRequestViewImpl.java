/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;

public class PasswordResetRequestViewImpl extends FlowPanel implements PasswordResetRequestView {

    private Presenter presenter;

    private final PasswordResetRequestForm form;

    public PasswordResetRequestViewImpl() {
        form = new PasswordResetRequestForm(new Command() {
            @Override
            public void execute() {
                if (!form.isValid()) {
                    form.setUnconditionalValidationErrorRendering(true);
                    throw new UserRuntimeException(form.getValidationResults().getValidationMessage(true, false));
                }
                presenter.requestPasswordReset(form.getValue());
            }
        });
        form.initContent();
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.populateNew();
        form.displayResetPasswordMessage(false);
        this.presenter.createNewCaptchaChallenge();
    }

    @Override
    public void reset() {
        form.reset();
    }

    @Override
    public void createNewCaptchaChallenge() {
        form.createNewCaptchaChallenge();
    }

    @Override
    public void displayPasswordResetFailedMessage() {
        form.displayResetPasswordMessage(true);
    }
}
