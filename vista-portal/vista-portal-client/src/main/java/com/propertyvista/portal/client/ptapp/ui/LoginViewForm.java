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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;
import com.pyx4j.security.rpc.AuthenticationRequest;

public class LoginViewForm extends CEntityForm<AuthenticationRequest> {

    public LoginViewForm() {
        super(AuthenticationRequest.class);
    }

    @Override
    public void createContent() {
        HTML header = new HTML("<h4>Login to Your Account</h4>");
        header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        header.getElement().getStyle().setMarginRight(2, Unit.EM);
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        FlowPanel main = new FlowPanel();
        main.add(header);
        main.add(new BasicWidgetDecorator(create(proto().email(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().password(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().captcha(), this)));
        setWidget(main);
    }

}