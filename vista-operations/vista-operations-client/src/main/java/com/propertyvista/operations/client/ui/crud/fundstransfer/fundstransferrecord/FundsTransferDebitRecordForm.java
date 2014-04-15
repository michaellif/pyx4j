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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;

public class FundsTransferDebitRecordForm extends OperationsEntityForm<FundsTransferRecordDTO> {

    private static final I18n i18n = I18n.get(FundsTransferDebitRecordForm.class);

    public FundsTransferDebitRecordForm(IForm<FundsTransferRecordDTO> view) {
        super(FundsTransferRecordDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().padBatch().pmc().name(), new FormDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().merchantTerminalId(), new FormDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile().fundsTransferType(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().padBatch().padFile().sent(), new FormDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().clientId(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().amount(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().bankId(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().branchTransitNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().accountNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().transactionId(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().acknowledgmentStatusCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processed(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processingStatus(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().statusChangeDate(), new FormDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Paid Or Rejected"));

        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().paymentDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reconciliationStatus(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reasonCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().reasonText(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().fee(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordPaidOrRejected().processingStatus(), new FormDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Return"));

        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().paymentDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reconciliationStatus(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reasonCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().reasonText(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().fee(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecordReturn().processingStatus(), new FormDecoratorBuilder().build()));

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
