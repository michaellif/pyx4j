/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;

public class BillingCycleLeaseForm extends CrmEntityForm<BillingCycleLeaseDTO> {

    private static final I18n i18n = I18n.get(BillingCycleLeaseForm.class);

    public BillingCycleLeaseForm() {
        super(BillingCycleLeaseDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setH2(++row, 0, 1, i18n.tr("Bills"));
        main.setWidget(++row, 0, ((BillingCycleLeaseView) getParentView()).getBillListerView().asWidget());

        return new ScrollPanel(main);
    }

}
