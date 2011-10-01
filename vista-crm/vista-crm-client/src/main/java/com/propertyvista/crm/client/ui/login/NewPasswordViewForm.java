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
package com.propertyvista.crm.client.ui.login;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationData.ShowMandatory;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.crm.client.ui.login.NewPasswordView.ConversationType;
import com.propertyvista.crm.rpc.PasswordChangeRequest;

public class NewPasswordViewForm extends CEntityEditor<PasswordChangeRequest> {

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

        DecorationData decor = new DecorationData(144, 152);
        decor.showMandatory = ShowMandatory.Mandatory;

        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().currentPassword()), decor));

        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().newPassword()), decor));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().newPassword2()), decor));
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