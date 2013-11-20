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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;

public class DirectDebitRecordForm extends OperationsEntityForm<DirectDebitRecord> {

    public DirectDebitRecordForm(IForm<DirectDebitRecord> view) {
        super(DirectDebitRecord.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().pmc().name())).customLabel("PMC:").build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().accountNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().amount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().paymentReferenceNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().customerName())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().receivedDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processingStatus())).build());

        panel.setH1(++row, 0, 2, "Trace");

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().trace().collectionDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().trace().locationCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().trace().sourceCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().trace().traceNumber())).build());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
