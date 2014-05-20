/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;

public class LeadsAndRentalsSummaryForm extends ZoomableViewForm<LeadsAndRentalsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(LeadsAndRentalsSummaryForm.class);

    public LeadsAndRentalsSummaryForm() {
        super(LeadsAndRentalsGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        DualColumnForm formPanel = new DualColumnForm(this);
        formPanel.append(Location.Left, proto().leads()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().appointmentsLabel()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().rentalsLabel()).decorate().componentWidth(200);
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        String appointmentslabel =//@formatter:off 
                getValue().leads().getValue() != 0 
                ? i18n.tr("{0} ({1,number,percent} of Leads)", 
                        getValue().appointments().getValue(), 
                        (double)getValue().appointments().getValue()/getValue().leads().getValue())
                : getValue().appointments().getValue().toString();
                //@formatter:on
        get(proto().appointmentsLabel()).setValue(appointmentslabel);

        String rentalsLabel =//@formatter:off
                getValue().appointments().getValue() != 0
                ? i18n.tr("{0} ({1,number,percent} of Appontments)",
                        getValue().rentals().getValue(),
                        (double)getValue().rentals().getValue()/getValue().appointments().getValue())
                : getValue().rentals().getValue().toString();
        //@formatter:on
        get(proto().rentalsLabel()).setValue(rentalsLabel);
    }
}
