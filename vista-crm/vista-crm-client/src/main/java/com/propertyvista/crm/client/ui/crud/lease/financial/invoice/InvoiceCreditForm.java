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
package com.propertyvista.crm.client.ui.crud.lease.financial.invoice;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceCreditDTO;

public class InvoiceCreditForm extends CrmEntityForm<InvoiceCreditDTO> {

    private static final I18n i18n = I18n.get(InvoiceCreditForm.class);

    public InvoiceCreditForm(IForm<InvoiceCreditDTO> view) {
        super(InvoiceCreditDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().item()).decorate();
        formPanel.append(Location.Left, proto().date()).decorate();
        formPanel.append(Location.Left, proto().totalAmount()).decorate();
        formPanel.append(Location.Left, proto().outstandingCredit()).decorate();

        formPanel.h2(i18n.tr("Links"));
        formPanel.append(Location.Dual, proto().debitCreditLinks(), new DebitCreditLinkFolder());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

}
