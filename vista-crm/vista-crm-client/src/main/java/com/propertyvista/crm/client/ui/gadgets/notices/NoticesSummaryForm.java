/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.notices;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;

public class NoticesSummaryForm extends CounterGadgetSummaryForm<NoticesGadgetDataDTO> {

    private static final I18n i18n = I18n.get(NoticesSummaryForm.class);

    public NoticesSummaryForm() {
        super(NoticesGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        panel.setH2(++row, 0, 1, i18n.tr("Vacancy:"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitVacancyLabel())).customLabel(i18n.tr("Units Vacant")).componentWidth(15).build());
        panel.setH2(++row, 0, 1, i18n.tr("Notices Leaving:"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingThisMonth())).customLabel(i18n.tr("This Month")).componentWidth(5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingNextMonth())).customLabel(i18n.tr("Next Month")).componentWidth(5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeaving60to90Days())).customLabel(i18n.tr("60 to 90 Days")).componentWidth(5)
                .build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingOver90Days())).customLabel(i18n.tr("90+")).componentWidth(5).build());
        return panel;

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        int vacant = getValue().vacantUnits().getValue();
        int total = getValue().totalUnits().getValue();
        double percent = total != 0 ? vacant / (double) total : 0d;
        get(proto().unitVacancyLabel()).setValue(i18n.tr("{0} of {1} ({2,number,percent})", vacant, total, percent));
    }
}
