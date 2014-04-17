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
package com.propertyvista.operations.client.ui.crud.simulator.pad.batch;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimDebitRecord;

public class DebitRecordEditor extends OperationsEntityForm<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordEditor(IForm<PadSimDebitRecord> view) {
        super(PadSimDebitRecord.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, inject(proto().amount(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().clientId(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().transactionId(), new FieldDecoratorBuilder(10).build()));
        content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, inject(proto().acknowledgmentStatusCode(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().reconciliationStatus(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().reasonCode(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().fee(), new FieldDecoratorBuilder(10).build()));

        row = -1;
        content.setWidget(++row, 1, inject(proto().bankId(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 1, inject(proto().branchTransitNumber(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 1, inject(proto().accountNumber(), new FieldDecoratorBuilder(10).build()));
        ++row;
        ++row;
        content.setWidget(++row, 1, inject(proto().paymentDate(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 1, inject(proto().reasonText(), new FieldDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().updated(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(content));
    }
}
