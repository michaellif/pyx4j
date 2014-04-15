/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceDebitDTO;

public class InvoiceDebitForm extends CrmEntityForm<InvoiceDebitDTO> {

    private static final I18n i18n = I18n.get(InvoiceCreditForm.class);

    public InvoiceDebitForm(IForm<InvoiceDebitDTO> view) {
        super(InvoiceDebitDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, inject(proto().item(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().date(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().totalAmount(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().outstandingDebit(), new FormDecoratorBuilder().build()));
        panel.setH2(++row, 0, 2, i18n.tr("Links"));
        panel.setWidget(++row, 0, 2, inject(proto().debitCreditLinks(), new DebitCreditLinkFolder()));
        selectTab(addTab(panel));
    }

}
