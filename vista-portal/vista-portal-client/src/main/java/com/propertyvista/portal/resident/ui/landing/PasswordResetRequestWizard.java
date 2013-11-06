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
package com.propertyvista.portal.resident.ui.landing;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.portal.resident.ui.CPortalEntityWizard;
import com.propertyvista.portal.resident.ui.util.decorators.LoginWidgetDecoratorBuilder;

public class PasswordResetRequestWizard extends CPortalEntityWizard<PasswordRetrievalRequest> {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    PasswordResetRequestWizard(PasswordResetRequestWizardViewImpl view) {
        super(PasswordRetrievalRequest.class, view, i18n.tr("Reset Password"), i18n.tr("Submit"), ThemeColor.contrast3);
        addStep(createStep());
    }

    public BasicFlexFormPanel createStep() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

        int row = -1;

        HTML message = new HTML(
                i18n.tr("If you've forgotten the password to your account, please enter the email address that you registered with and your password will be emailed to you shortly."));
        message.getElement().getStyle().setProperty("maxWidth", "600px");
        message.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        mainPanel.setWidget(++row, 0, message);
        mainPanel.setBR(++row, 0, 1);
        mainPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().email())).build());
        mainPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(inject(proto().captcha()))
                .watermark(LandingViewImpl.i18n.tr("Enter both security words above")).build());
        mainPanel.setBR(++row, 0, 1);

        return mainPanel;
    }

    public void createNewCaptchaChallenge() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.createNewChallenge();
    }

}
