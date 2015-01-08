/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-21
 * @author VladL
 */
package com.propertyvista.crm.client.ui.components.boxes;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.MiscUtils;

public abstract class ReasonBox extends OkCancelDialog {

    private static final I18n i18n = I18n.get(ReasonBox.class);

    private final CTextArea reason = new CTextArea();

    public ReasonBox(String title) {
        super(title);
        setBody(createBody());
        setDialogPixelWidth(350);
    }

    protected Widget createBody() {
        VerticalPanel content = new VerticalPanel();
        content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        content.add(new HTML(i18n.tr("Please fill the reason") + ":"));
        content.add(reason);

        MiscUtils.setPanelSpacing(content, 8);
        reason.asWidget().getElement().getStyle().setPaddingLeft(6, Unit.PX);
        reason.asWidget().getElement().getStyle().setPaddingRight(8, Unit.PX);
        content.setWidth("100%");
        return content.asWidget();
    }

    public String getReason() {
        return reason.getValue();
    }
}