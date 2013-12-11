/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.l1.L1OwedNsfCharges;
import com.propertyvista.domain.legal.l1.NsfChargeDetails;

public class L1OwedNsfChargesForm extends CEntityForm<L1OwedNsfCharges> {

    public L1OwedNsfChargesForm() {
        super(L1OwedNsfCharges.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().nsfChargesBreakdown(), new L1NsfChargesBreakdownFolder() {
            @Override
            public void onTotalChargeChanged() {
                updateTotalCharge();
            }
        }));
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().nsfTotalChargeOwed())).build());
        get(proto().nsfTotalChargeOwed()).setViewable(true);
        get(proto().nsfTotalChargeOwed()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
            @Override
            public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                onTotalUpdated();
            }
        });
        return panel;
    }

    private void updateTotalCharge() {
        BigDecimal total = BigDecimal.ZERO;
        for (NsfChargeDetails d : getValue().nsfChargesBreakdown()) {
            total = total.add(d.totalCharge().getValue());
        }
        get(proto().nsfTotalChargeOwed()).setValue(total, true, true);
    }

    public void onTotalUpdated() {

    }

}
