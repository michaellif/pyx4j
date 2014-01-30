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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.security.rpc.AuthenticationRequest;

@Deprecated
// TODO replaces LoginForm with this component,  and all the buttons etc, should be created in the view
public class AuthenticationRequestForm extends CEntityForm<AuthenticationRequest> {

    public AuthenticationRequestForm() {
        super(AuthenticationRequest.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new LoginPanelWidgetDecorator(inject(proto().email())));
        content.setWidget(++row, 0, new LoginPanelWidgetDecorator(inject(proto().password())));
        content.setWidget(++row, 0, new LoginPanelWidgetDecorator(inject(proto().captcha())));

        return content;
    }

    /**
     * Creates new challenge and makes captcha visible.
     */
    public void reEnableCaptcha() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.createNewChallenge();
        captcha.setVisible(true);
    }

    /**
     * Renders captcha invisible.
     */
    public void disableCaptcha() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.setVisible(false);
    }
}
