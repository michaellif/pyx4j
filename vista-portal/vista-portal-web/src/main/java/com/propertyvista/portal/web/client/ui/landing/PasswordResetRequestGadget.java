/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView.PasswordResetRequestPresenter;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.LoginDecoratorBuilder;

public class PasswordResetRequestGadget extends AbstractGadget<PasswordResetRequestViewImpl> {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private PasswordResetRequestPresenter presenter;

    private final PasswordResetRequestForm form;

    PasswordResetRequestGadget(PasswordResetRequestViewImpl view) {
        super(view, null, "Reset Password", ThemeColor.contrast3);
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        setActionsToolbar(new PasswordResetRequestToolbar());

        FlowPanel contentPanel = new FlowPanel();

        form = new PasswordResetRequestForm();
        form.initContent();
        contentPanel.add(form);

        setContent(contentPanel);
    }

    public void setPresenter(PasswordResetRequestPresenter presenter) {
        this.presenter = presenter;
        form.populateNew();
        form.displayResetPasswordMessage(false);
        presenter.createNewCaptchaChallenge();
    }

    public void reset() {
        form.reset();
    }

    public void createNewCaptchaChallenge() {
        form.createNewCaptchaChallenge();
    }

    public void displayResetPasswordMessage(boolean b) {
        form.displayResetPasswordMessage(b);
    }

    class PasswordResetRequestForm extends CEntityForm<PasswordRetrievalRequest> {

        private final HTML passwordResetFailedMessage;

        public PasswordResetRequestForm() {
            super(PasswordRetrievalRequest.class);
            this.passwordResetFailedMessage = new HTML(i18n.tr("Failed to reset password. Check that email and captcha you provided are correct."));

        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel();

            int row = -1;
            flexPanel.setWidget(++row, 0, new HTML("Please enter the email address you registered with:"));
            flexPanel.setWidget(++row, 0, new LoginDecoratorBuilder(inject(proto().email())).build());
            flexPanel.setWidget(++row, 0,
                    new LoginDecoratorBuilder(inject(proto().captcha())).watermark(LandingViewImpl.i18n.tr("Enter both security words above")).build());
            flexPanel.setWidget(++row, 0, passwordResetFailedMessage);
            passwordResetFailedMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
            passwordResetFailedMessage.setVisible(false);

            return flexPanel;
        }

        public void createNewCaptchaChallenge() {
            CCaptcha captcha = (CCaptcha) get(proto().captcha());
            captcha.createNewChallenge();
        }

        public void displayResetPasswordMessage(boolean display) {
            passwordResetFailedMessage.setVisible(display);
        }

        @Override
        public void populateNew() {
            displayResetPasswordMessage(false);
            super.populateNew();
        }
    }

    class PasswordResetRequestToolbar extends Toolbar {

        private final Button requestButton;

        public PasswordResetRequestToolbar() {

            requestButton = new Button(i18n.tr("RESET"), new Command() {
                @Override
                public void execute() {
                    if (!form.isValid()) {
                        form.setUnconditionalValidationErrorRendering(true);
                        throw new UserRuntimeException(form.getValidationResults().getValidationMessage(true, false));
                    }
                    presenter.requestPasswordReset(form.getValue());
                }
            });
            requestButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(requestButton);

        }
    }

}
