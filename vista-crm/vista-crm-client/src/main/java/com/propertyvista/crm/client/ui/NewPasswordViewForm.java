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
package com.propertyvista.crm.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.common.client.ui.VistaWidgetDecorator;
import com.propertyvista.crm.client.ui.NewPasswordView.ConversationType;
import com.propertyvista.crm.rpc.PasswordChangeRequest;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.CEntityForm;

public class NewPasswordViewForm extends CEntityForm<PasswordChangeRequest> {

    private static I18n i18n = I18nFactory.getI18n(NewPasswordViewForm.class);

    private HTML header;

    public NewPasswordViewForm() {
        super(PasswordChangeRequest.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        header = new HTML();
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.add(header);

        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().currentPassword()), 144, 152));

        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().newPassword()), 144, 152));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().newPassword2()), 144, 152));
        setWidget(main);
        return main;
    }

    public void setConversationType(ConversationType type) {

        switch (type) {
        case CHANGE:
            header.setHTML(HtmlUtils.h2(i18n.tr("Change Password")));
            get(proto().currentPassword()).setVisible(true);
            break;
        case RESET:
            header.setHTML(HtmlUtils.h2(i18n.tr("Create Password")));
            get(proto().currentPassword()).setVisible(false);
            break;
        }
    }
}