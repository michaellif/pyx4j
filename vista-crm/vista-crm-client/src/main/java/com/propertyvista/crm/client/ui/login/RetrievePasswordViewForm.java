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
import com.pyx4j.entity.client.ui.flex.CEntityForm;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationData.ShowMandatory;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.crm.rpc.PasswordRetrievalRequest;

public class RetrievePasswordViewForm extends CEntityForm<PasswordRetrievalRequest> {

    private static I18n i18n = I18nFactory.getI18n(RetrievePasswordViewForm.class);

    public RetrievePasswordViewForm() {
        super(PasswordRetrievalRequest.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        HTML header = new HTML(HtmlUtils.h2(i18n.tr("Retrieve Password")));
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        DecorationData decor = new DecorationData(90, 160);
        decor.showMandatory = ShowMandatory.None;

        main.add(header);
        main.add(new VistaWidgetDecorator(inject(proto().email()), decor));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().captcha()), decor));
        return main;
    }
}