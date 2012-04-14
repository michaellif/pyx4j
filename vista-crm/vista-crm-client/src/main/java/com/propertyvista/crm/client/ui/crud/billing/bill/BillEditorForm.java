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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.dto.BillDTO;

public class BillEditorForm extends CrmEntityForm<BillDTO> {

    private static final I18n i18n = I18n.get(BillEditorForm.class);

    public BillEditorForm() {
        this(false);
    }

    public BillEditorForm(boolean viewMode) {
        super(BillDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setH1(++row, 0, 2, i18n.tr("Info"));
        int row2 = row;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().lease().unit()), 20).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().building()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().billingPeriodStartDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().billingPeriodEndDate()), 10).build());

        ++row2;
        main.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().billingRun().billingCycle().paymentFrequency()), 15).build());
        main.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().billingRun().billingCycle().billingPeriodStartDay()), 5).build());
        main.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().billingRun().billingCycle().billingRunTargetDay()), 5).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().executionTargetDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().executionDate()), 10).build());

        main.setH1(++row, 0, 2, i18n.tr("Status"));
        row2 = row;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billSequenceNumber()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billType()), 10).build());

        main.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().billStatus()), 10).build());
        main.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().rejectReason()), 15).build());

        main.setH1(++row, 0, 2, i18n.tr("Last Bill"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().previousBalanceAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentReceivedAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositRefundAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().immediateAdjustments()), 10).build());

        main.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().serviceCharge()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recurringFeatureCharges()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recurringProductCharges(), new FeatureChargesFolder()), 30).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().oneTimeFeatureCharges()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalAdjustments()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().credits()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().taxes()), 10).build());

        // Dues: 
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalDueAmount()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().pastDueAmount()), 10).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().rejectReason()).setVisible(getValue().billStatus().getValue() == BillStatus.Rejected);
    }
}