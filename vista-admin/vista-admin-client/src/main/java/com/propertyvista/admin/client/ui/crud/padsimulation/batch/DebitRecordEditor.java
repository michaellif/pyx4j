/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimDebitRecord;

public class DebitRecordEditor extends AdminEntityForm<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordEditor() {
        this(false);
    }

    public DebitRecordEditor(boolean viewMode) {
        super(PadSimDebitRecord.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().clientId()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transactionId()), 10).build());
        content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reconciliationStatus()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reasonCode()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fee()), 10).build());

        row = -1;
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().bankId()), 10).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().accountNumber()), 10).build());
        ++row;
        ++row;
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentDate()), 10).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reasonText()), 10).build());

        selectTab(addTab(content));
    }
}
