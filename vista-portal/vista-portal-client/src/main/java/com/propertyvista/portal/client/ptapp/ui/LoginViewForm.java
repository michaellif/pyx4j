/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.security.rpc.AuthenticationRequest;

public class LoginViewForm extends CEntityForm<AuthenticationRequest> {

    private static I18n i18n = I18nFactory.getI18n(LoginViewForm.class);

    public LoginViewForm() {
        super(AuthenticationRequest.class);
    }

    @Override
    public void createContent() {
        HTML header = new HTML("<h2>Login to Your Account</h2>");
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        FlowPanel main = new FlowPanel();
        main.add(header);
        main.add(new VistaWidgetDecorator(create(proto().email(), this), 62, 152));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto().password(), this), 62, 152));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto().captcha(), this), 62, 152));
        setWidget(main);
    }
}