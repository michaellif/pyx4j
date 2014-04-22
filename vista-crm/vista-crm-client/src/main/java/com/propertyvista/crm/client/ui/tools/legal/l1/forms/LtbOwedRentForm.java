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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.legal.ltbcommon.LtbOwedRent;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

public class LtbOwedRentForm extends CForm<LtbOwedRent> {

    public LtbOwedRentForm() {
        super(LtbOwedRent.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().rentOwingBreakdown(), new LtbRentOwedBreakdownFolder() {
            @Override
            public void onTotalOwedRentChanged() {
                LtbOwedRentForm.this.updateTotal();
            }
        }));
        panel.setWidget(++row, 0, 1, inject(proto().totalRentOwing(), new FieldDecoratorBuilder().build()));
        get(proto().totalRentOwing()).setViewable(true);
        return panel;
    }

    public void onTotalUpdated() {

    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (RentOwingForPeriod rentOwingForPeriod : getValue().rentOwingBreakdown()) {
            total = total.add(rentOwingForPeriod.rentOwing().getValue(BigDecimal.ZERO));
        }
        get(proto().totalRentOwing()).setValue(total, false, true);

        onTotalUpdated();
    }

}
