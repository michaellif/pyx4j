/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;

public class DirectDebitRecordForm extends OperationsEntityForm<DirectDebitRecord> {

    public DirectDebitRecordForm(IForm<DirectDebitRecord> view) {
        super(DirectDebitRecord.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().pmc().name(), new FormDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().accountNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().amount(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().paymentReferenceNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().customerName(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().receivedDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processingStatus(), new FormDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().operationsNotes(), new FormDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, "Trace");

        panel.setWidget(++row, 0, 1, inject(proto().trace().collectionDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().trace().locationCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().trace().sourceCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().trace().traceNumber(), new FormDecoratorBuilder().build()));

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
