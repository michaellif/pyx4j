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
package com.propertyvista.portal.shared.ui.landing;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.LoginFormPanel;

public class PasswordResetRequestWizard extends CPortalEntityWizard<PasswordRetrievalRequest> {

    static final I18n i18n = I18n.get(PasswordResetRequestWizard.class);

    PasswordResetRequestWizard(PasswordResetRequestWizardViewImpl view) {
        super(PasswordRetrievalRequest.class, view, i18n.tr("Reset Password"), i18n.tr("Submit"), ThemeColor.contrast3);
        addStep(createStep(), i18n.tr("General"));
    }

    public IsWidget createStep() {
        LoginFormPanel formPanel = new LoginFormPanel(this);

        HTML message = new HTML(
                i18n.tr("If you've forgotten the password to your account, please enter the email address that you registered with and your password will be emailed to you shortly."));
        message.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        message.getElement().getStyle().setProperty("maxWidth", "500px");
        message.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        formPanel.append(Location.Left, message);
        formPanel.br();
        formPanel.append(Location.Left, proto().email()).decorate();
        formPanel.append(Location.Left, proto().captcha()).decorate();

        ((CEmailField) get(proto().email())).setWatermark(get(proto().email()).getTitle());
        ((CCaptcha) get(proto().captcha())).setWatermark(i18n.tr("Enter both security words above"));

        formPanel.br();

        return formPanel;
    }

    public void createNewCaptchaChallenge() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.createNewChallenge();
    }

}
