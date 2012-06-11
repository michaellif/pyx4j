/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-11
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

class BillingCycleBillLister extends BillLister {

    private static final I18n i18n = I18n.get(BillingCycleBillLister.class);

    public BillingCycleBillLister() {
        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Confirm Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to Confirm checked items?"), new ConfirmDecline() {
                    @Override
                    public void onConfirmed() {
                        for (BillDataDTO item : getDataTablePanel().getDataTable().getCheckedItems()) {

                            // TODO action here... 
                        }
                    }

                    @Override
                    public void onDeclined() {
                    }
                });
            }
        }));
    }

}
