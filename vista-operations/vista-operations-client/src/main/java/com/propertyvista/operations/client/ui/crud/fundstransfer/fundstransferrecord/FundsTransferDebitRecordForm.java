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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferrecord;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;

public class FundsTransferDebitRecordForm extends OperationsEntityForm<FundsTransferRecordDTO> {

    private static final I18n i18n = I18n.get(FundsTransferDebitRecordForm.class);

    public FundsTransferDebitRecordForm(IForm<FundsTransferRecordDTO> view) {
        super(FundsTransferRecordDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().padBatch().pmc().name(), new FieldDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().merchantTerminalId(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile().fundsTransferType(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile().sent(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().clientId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().amount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().bankId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().branchTransitNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().accountNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().transactionId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().acknowledgmentStatusCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processed(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processingStatus(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().statusChangeDate(), new FieldDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Paid Or Rejected"));

        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().paymentDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reconciliationStatus(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reasonCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reasonText(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().fee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().processingStatus(), new FieldDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Return"));

        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().paymentDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reconciliationStatus(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reasonCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reasonText(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().fee(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().processingStatus(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
