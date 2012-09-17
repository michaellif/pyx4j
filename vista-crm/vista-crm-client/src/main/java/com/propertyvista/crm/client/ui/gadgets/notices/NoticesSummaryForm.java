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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Utils;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;

public class NoticesSummaryForm extends CounterGadgetSummaryForm<NoticesGadgetDataDTO> {

    private static final I18n i18n = I18n.get(NoticesSummaryForm.class);

    public NoticesSummaryForm() {
        super(NoticesGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        if (false) {
            final double MARGIN = 1.5;

            VerticalPanel content = new VerticalPanel();
            content.setWidth("100%");
            content.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            content.getElement().getStyle().setPaddingBottom(1, Unit.EM);

            FlexTable vacancy = Utils.createTable(//@formatter:off
                new String[] {"", ""},
                new String[] {"100", "100"},
                new String[] {i18n.tr("Units Vacant:")},
                new Widget[][] {{
                    inject(proto().unitsVacant()).asWidget(),
                }}
        );//@formatter:on
            vacancy.getElement().getStyle().setMarginBottom(MARGIN, Unit.EM);
            content.add(vacancy);

            FlexTable notices = Utils.createTable(//@formatter:off
                new String[] {"", i18n.tr("This Month"), i18n.tr("Next Month"), i18n.tr("90+")},
                new String[] {"100", "100", "100", "100"},
                new String[] {i18n.tr("Notices Leaving:")},
                new Widget[][] {{
                    inject(proto().noticesLeavingThisMonth()).asWidget(),
                    inject(proto().noticesLeavingNextMonth()).asWidget(),
                    inject(proto().noticesLeavingOver90Days()).asWidget()
                }}
        );//@formatter:off
        content.add(notices);
        return content;
        } else {
            FormFlexPanel panel = new FormFlexPanel();
            int row = -1;
            panel.setH2(++row, 0, 1, i18n.tr("Vacancy:"));
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitsVacant())).componentWidth(5).build());
            panel.setH2(++row, 0, 1, i18n.tr("Notices Leaving:"));
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingThisMonth())).customLabel(i18n.tr("This Month")).componentWidth(5).build());
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingNextMonth())).customLabel(i18n.tr("Next Month")).componentWidth(5).build());
            panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noticesLeavingOver90Days())).customLabel(i18n.tr("90+")).componentWidth(5).build());
            return panel;
        }
        
    }
}
