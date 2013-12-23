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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimDebitRecord;

public class DebitRecordEditor extends OperationsEntityForm<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordEditor(IForm<PadSimDebitRecord> view) {
        super(PadSimDebitRecord.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().clientId()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().transactionId()), 10).build());
        content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reconciliationStatus()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reasonCode()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().fee()), 10).build());

        row = -1;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().bankId()), 10).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().accountNumber()), 10).build());
        ++row;
        ++row;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().paymentDate()), 10).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().reasonText()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updated())).build());

        selectTab(addTab(content));
    }
}
