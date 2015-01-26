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
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.legal.ltbcommon.LtbOwedRent;
import com.propertyvista.domain.legal.n4.pdf.N4PdfRentOwingForPeriod;

public class LtbOwedRentForm extends CForm<LtbOwedRent> {

    public LtbOwedRentForm() {
        super(LtbOwedRent.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel panel = new FormPanel(this);
        panel.append(Location.Left, proto().rentOwingBreakdown(), new LtbRentOwedBreakdownFolder() {
            @Override
            public void onTotalOwedRentChanged() {
                LtbOwedRentForm.this.updateTotal();
            }
        });
        panel.append(Location.Left, proto().totalRentOwing()).decorate();
        get(proto().totalRentOwing()).setViewable(true);
        return panel;
    }

    public void onTotalUpdated() {

    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (N4PdfRentOwingForPeriod rentOwingForPeriod : getValue().rentOwingBreakdown()) {
            total = total.add(rentOwingForPeriod.rentOwing().getValue(BigDecimal.ZERO));
        }
        get(proto().totalRentOwing()).setValue(total, false, true);

        onTotalUpdated();
    }

}
