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
package com.propertyvista.common.client.ui.components.editors.dto.bill;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.dto.BillDTO;

public class BillForm extends CEntityDecoratableForm<BillDTO> {

    private static final I18n i18n = I18n.get(BillForm.class);

    public BillForm() {
        this(true);
    }

    public BillForm(boolean viewMode) {
        super(BillDTO.class);

        if (viewMode) {
            setEditable(false);
            setViewable(true);
        }
    }

    @Override
    public IsWidget createContent() {

        // form top panel:
        FormFlexPanel top = new FormFlexPanel();
        int row = -1;

        top.setH1(++row, 0, 2, i18n.tr("Info"));
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().lease().unit()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().building()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().billingPeriodStartDate()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().billingPeriodEndDate()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().billingType().paymentFrequency()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingRun().executionTargetDate()), 20).build());

        top.setH1(++row, 0, 2, i18n.tr("Status"));
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billSequenceNumber()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billType()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billStatus()), 20).build());
        top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejectReason()), 20).build());

        // form bottom panel:
        FormFlexPanel bottom = new FormFlexPanel();
        row = -1;

        bottom.setH1(++row, 0, 2, i18n.tr("Last Bill"));
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balanceForwardAmount())).build());

        bottom.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsableViewer()));
        bottom.setHR(++row, 0, 2);
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pastDueAmount())).build());

        bottom.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        bottom.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsableViewer()));

        bottom.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productCreditAmount())).build());

        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentAmount())).build());
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().taxes())).build());

        // Dues:
        bottom.setHR(++row, 0, 2);
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalDueAmount())).build());

        bottom.getColumnFormatter().setWidth(0, "50%");
        bottom.getColumnFormatter().setWidth(1, "50%");

        // form main panel:
        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, top);
        main.setWidget(1, 0, bottom);

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().rejectReason()).setVisible(getValue().billStatus().getValue() == BillStatus.Rejected);
    }

    protected class DecoratorBuilder extends WidgetDecorator.Builder { //builder specifically for this form (as it uses mixed formats)

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
            readOnlyMode(!isEditable());
            componentAlignment(Alignment.right);
            labelAlignment(Alignment.left);
            useLabelSemicolon(false);
            componentWidth(30);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
            componentAlignment(Alignment.right);
            labelAlignment(Alignment.left);
        }
    }
}