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
package com.propertyvista.portal.ptapp.client.ui;

import static com.pyx4j.commons.HtmlUtils.h2;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class LoginViewForm extends CEntityEditor<AuthenticationRequest> {

    private static I18n i18n = I18n.get(LoginViewForm.class);

    public LoginViewForm() {
        super(AuthenticationRequest.class);
    }

    @Override
    public IsWidget createContent() {
        HTML welcome = new HTML(HtmlUtils.h4(i18n.tr("Welcome to") + "[PMC name]" + "!"));
        welcome.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        HTML loginNotes = new HTML(PortalResources.INSTANCE.loginNotes().getText());

        HTML header = new HTML(h2(i18n.tr("Login")));
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        FlowPanel main = new FlowPanel();
        main.add(welcome);
        main.add(loginNotes);
        main.add(header);
        main.add(new VistaWidgetDecorator(inject(proto().email()), 90, 160));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().password()), 90, 160));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().captcha()), 90, 160));
        return main;
    }
}