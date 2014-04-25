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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationrecord;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;

public class FundsReconciliationDebitRecordForm extends OperationsEntityForm<FundsReconciliationRecordRecordDTO> {

    private static final I18n i18n = I18n.get(FundsReconciliationDebitRecordForm.class);

    public FundsReconciliationDebitRecordForm(IForm<FundsReconciliationRecordRecordDTO> view) {
        super(FundsReconciliationRecordRecordDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1,
                inject(proto().reconciliationSummary().merchantAccount().pmc().name(), new FieldDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationSummary().reconciliationFile(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationSummary().reconciliationFile().fundsTransferType(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().merchantTerminalId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().paymentDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().clientId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().transactionId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().amount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationStatus(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reasonCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reasonText(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().fee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processingStatus(), new FieldDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Funds Transfer Record"));

        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().bankId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().branchTransitNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().accountNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().transactionId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().acknowledgmentStatusCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().processed(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().debitRecord().processingStatus(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(panel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
