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
package com.propertyvista.crm.client.ui.crud.billing.bill;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public class BillDataForm extends CrmEntityForm<BillDataDTO> {

    private static final I18n i18n = I18n.get(BillDataForm.class);

    private final boolean justPreviewBill;

    public BillDataForm(IForm<BillDataDTO> view, boolean justCurrentBill) {
        super(BillDataDTO.class, view);
        setEditable(true);
        setViewable(false);
        this.justPreviewBill = justCurrentBill;
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        content.setWidget(0, 0, inject(proto().bill(), new BillForm(justPreviewBill)));
        selectTab(addTab(content));
    }

}