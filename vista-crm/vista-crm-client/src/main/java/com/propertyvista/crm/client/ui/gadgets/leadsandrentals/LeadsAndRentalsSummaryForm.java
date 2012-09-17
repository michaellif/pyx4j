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
package com.propertyvista.crm.client.ui.gadgets.leadsandrentals;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;

public class LeadsAndRentalsSummaryForm extends CounterGadgetSummaryForm<LeadsAndRentalsGadgetDataDTO> {

    public LeadsAndRentalsSummaryForm() {
        super(LeadsAndRentalsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leads())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().appointmentsLabel())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rentalsLabel())).build());

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        String appointmentslabel =//@formatter:off 
                getValue().leads().getValue() != 0 
                ? SimpleMessageFormat.format("{0} ({1,number,percent} of Leads)", 
                        getValue().appointments().getValue(), 
                        (double)getValue().appointments().getValue()/getValue().leads().getValue())
                : getValue().appointments().getValue().toString();
                //@formatter:on

        get(proto().appointmentsLabel()).setValue(appointmentslabel);

    }
}
