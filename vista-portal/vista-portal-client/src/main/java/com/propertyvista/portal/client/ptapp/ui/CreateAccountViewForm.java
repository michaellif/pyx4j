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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

public class CreateAccountViewForm extends CEntityForm<AccountCreationRequest> {

    public CreateAccountViewForm() {
        super(AccountCreationRequest.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new HTML("<h4>Create an Account</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().email(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().password(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(proto().captcha(), this)));
        setWidget(main);
    }

}