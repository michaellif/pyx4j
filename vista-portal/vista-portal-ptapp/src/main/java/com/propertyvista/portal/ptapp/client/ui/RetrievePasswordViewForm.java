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
package com.propertyvista.portal.ptapp.client.ui;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.rpc.ptapp.PasswordRetrievalRequest;

public class RetrievePasswordViewForm extends CEntityEditor<PasswordRetrievalRequest> {

    private static I18n i18n = I18n.get(RetrievePasswordViewForm.class);

    public RetrievePasswordViewForm() {
        super(PasswordRetrievalRequest.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        HTML header = new HTML(HtmlUtils.h2(i18n.tr("Retrieve Password")));
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);
        main.add(header);
        main.add(new VistaWidgetDecorator(inject(proto().email()), 90, 160));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(inject(proto().captcha()), 90, 160));
        return main;
    }
}