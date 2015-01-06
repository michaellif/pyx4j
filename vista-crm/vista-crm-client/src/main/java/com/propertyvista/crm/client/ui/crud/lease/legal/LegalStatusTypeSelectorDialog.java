/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-04
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.MiscUtils;
import com.propertyvista.domain.legal.LegalStatus;

public class LegalStatusTypeSelectorDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(LegalStatusTypeSelectorDialog.class);

    private final ListBox selector = new ListBox();

    public LegalStatusTypeSelectorDialog() {
        super(i18n.tr("Legal Status Type"));

        for (LegalStatus.Status s : LegalStatus.Status.values()) {
            if (s != LegalStatus.Status.None) {
                selector.addItem(s.toString(), s.name());
            }
        }

        setBody(createBody());
        setDialogPixelWidth(300);
    }

    protected Widget createBody() {
        VerticalPanel content = new VerticalPanel();
        content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        content.add(new HTML(i18n.tr("Please select Legal Status Type") + ":"));
        content.add(selector);

        MiscUtils.setPanelSpacing(content, 8);
        content.setWidth("100%");
        return content.asWidget();
    }

    @Override
    public boolean onClickOk() {
        if (selector.getSelectedIndex() != -1) {
            onSelected(LegalStatus.Status.valueOf(selector.getValue(selector.getSelectedIndex())));
            return true;
        }
        return false;
    }

    public void onSelected(LegalStatus.Status value) {

    }
}
