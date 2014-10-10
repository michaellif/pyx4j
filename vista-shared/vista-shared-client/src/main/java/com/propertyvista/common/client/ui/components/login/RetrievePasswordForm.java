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
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CaptchaComposite;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;

public class RetrievePasswordForm extends CForm<PasswordRetrievalRequest> {

    private static final I18n i18n = I18n.get(RetrievePasswordForm.class);

    private final HTML passwordResetFailedMessage;

    private final PasswordResetRequestView view;

    public RetrievePasswordForm(PasswordResetRequestView view) {//Command onSubmitCommand) {
        super(PasswordRetrievalRequest.class);
        this.view = view;
        this.passwordResetFailedMessage = new HTML(i18n.tr("Failed to reset password. Check that email and captcha you provided are correct."));
        asWidget().setWidth("30em");
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);

    }

    @Override
    protected IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        HTML header = new HTML(HtmlUtils.h2(i18n.tr("Reset Password")));
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        main.add(header);
        main.add(inject(proto().email(), new LoginPanelWidgetDecorator()));
        main.add(new HTML());
        main.add(inject(proto().captcha(), new LoginPanelWidgetDecorator(30)));
        get(proto().captcha()).setVisible(CaptchaComposite.isPublicKeySet());
        main.add(passwordResetFailedMessage);
        passwordResetFailedMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
        passwordResetFailedMessage.setVisible(false);

        Button retrievePasswordButton = createResetButton();

        retrievePasswordButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        retrievePasswordButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        retrievePasswordButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        main.add(retrievePasswordButton);

        main.getElement().getStyle().setMarginTop(1, Unit.EM);
        main.getElement().getStyle().setMarginBottom(1, Unit.EM);

        return main;
    }

    private Button createResetButton() {
        final Button submitButton = new Button(i18n.tr("Reset"), new Command() {

            @Override
            public void execute() {
                setVisitedRecursive();
                if (isValid()) {
                    view.getPresenter().requestPasswordReset(getValue());
                } else {
                    // here we hope that because the focus left the form and moved to submitButton,
                    // we get the relevant validation error on the form.
                }
            }
        });
        return submitButton;
    }

    public void createNewCaptchaChallenge() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.setVisible(true);
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