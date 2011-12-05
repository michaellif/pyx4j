/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.misc.EquifaxResult;

public class EquifaxResultEditorForm extends CrmEntityForm<EquifaxResult> {

    private static I18n i18n = I18n.get(EquifaxResultEditorForm.class);

    static String resultApprovePath = "mockup/equifax/approve.html";

    static String resultMoreInfoPath = "mockup/equifax/moreInfo.html";

    static String resultDeclinePath = "mockup/equifax/decline.html";

    private final SimplePanel resultHolder = new SimplePanel();

    public EquifaxResultEditorForm() {
        super(EquifaxResult.class, new CrmEditorsComponentFactory());
    }

    public EquifaxResultEditorForm(IEditableComponentFactory factory) {
        super(EquifaxResult.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        resultHolder.setSize("100%", "60em");

        int row = -1;
        main.setWidget(++row, 0, resultHolder);

        main.setSize("100%", "100%");
        return main;
    }

    @Override
    public void populate(EquifaxResult entity) {

        switch (entity.suggestedDecision().getValue()) {
        case Approve:
            resultHolder.setWidget(new Frame(resultApprovePath));
            break;
        case RequestInfo:
            resultHolder.setWidget(new Frame(resultMoreInfoPath));
            break;
        case Decline:
            resultHolder.setWidget(new Frame(resultDeclinePath));
            break;
        default:
            resultHolder.setWidget(new HTML(i18n.tr("There are no check results available yet.")));
            resultHolder.getWidget().getElement().getStyle().setProperty("textAlign", "center");
            resultHolder.getWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
            resultHolder.getWidget().getElement().getStyle().setFontSize(1.3, Unit.EM);
        }

        resultHolder.getWidget().setSize("100%", "100%");
        resultHolder.getWidget().getElement().getStyle().setBorderStyle(BorderStyle.NONE);

        super.populate(entity);
    }
}