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

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().pmc().name())).customLabel("PMC:").build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().merchantTerminalId())).build());

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().padFile())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().padFile().fundsTransferType())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().padFile().sent())).build());

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().clientId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().amount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().bankId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().branchTransitNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().accountNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().transactionId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatusCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processed())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processingStatus())).build());

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Paid Or Rejected"));

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().paymentDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().reconciliationStatus())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().reasonCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().reasonText())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().fee())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordPaidOrRejected().processingStatus())).build());

        panel.setH1(++row, 0, 2, i18n.tr("Reconciliation Record Return"));

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().paymentDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().reconciliationStatus())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().reasonCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().reasonText())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().fee())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecordReturn().processingStatus())).build());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
