/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;

final class LeaseExpirationSummaryForm extends CounterGadgetSummaryForm<LeaseExpirationGadgetDataDTO> {

    private static final I18n i18n = I18n.get(LeaseExpirationSummaryForm.class);

    public LeaseExpirationSummaryForm() {
        super(LeaseExpirationGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH2(++row, 0, 1, i18n.tr("Occupancy:"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitOccupancy())).customLabel(i18n.tr("Units Occupied")).componentWidth(5).build());
        content.setH2(++row, 0, 1, i18n.tr("Leases On Month to Month:"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesOnMonthToMonth())).customLabel("").componentWidth(5)
                .useLabelSemicolon(false).build());
        content.setH2(++row, 0, 1, i18n.tr("Leases Ending:"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingThisMonth())).customLabel(i18n.tr("This Month")).componentWidth(5)
                .build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingNextMonth())).customLabel(i18n.tr("Next Month")).componentWidth(5)
                .build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfLeasesEndingOver90Days())).customLabel(i18n.tr("90+ Days")).componentWidth(5)
                .build());

        return content;

    }
}