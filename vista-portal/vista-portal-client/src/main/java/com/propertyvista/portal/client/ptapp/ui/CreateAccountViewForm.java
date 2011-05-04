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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

public class CreateAccountViewForm extends CEntityForm<AccountCreationRequest> {

    public CreateAccountViewForm() {
        super(AccountCreationRequest.class);
    }

    @Override
    public IsWidget createContent() {
        HTML header = new HTML("<h2>Create an Account</h2>");
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        FlowPanel main = new FlowPanel();
        main.add(header);
        main.add(new VistaWidgetDecorator(inject(proto().email()), 90, 160));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().password()), 90, 160));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().captcha()), 90, 160));
        return main;
    }

}