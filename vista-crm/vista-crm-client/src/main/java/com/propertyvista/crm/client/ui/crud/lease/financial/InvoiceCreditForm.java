/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceCreditDTO;

public class InvoiceCreditForm extends CrmEntityForm<InvoiceCreditDTO> {

    private static final I18n i18n = I18n.get(InvoiceCreditForm.class);

    public InvoiceCreditForm(IForm<InvoiceCreditDTO> view) {
        super(InvoiceCreditDTO.class, view);

        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().date())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalAmount())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingCredit())).build());
        panel.setH2(++row, 0, 1, i18n.tr("Debit Links"));
        panel.setWidget(++row, 0, inject(proto().debitCreditLinks(), new DebitCreditLinkFolder()));
        selectTab(addTab(panel));
    }

}
