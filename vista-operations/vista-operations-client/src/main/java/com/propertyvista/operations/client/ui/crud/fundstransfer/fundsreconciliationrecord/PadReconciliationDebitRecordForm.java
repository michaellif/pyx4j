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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;

public class PadReconciliationDebitRecordForm extends OperationsEntityForm<FundsReconciliationRecordRecordDTO> {

    private static final I18n i18n = I18n.get(PadReconciliationDebitRecordForm.class);

    public PadReconciliationDebitRecordForm(IForm<FundsReconciliationRecordRecordDTO> view) {
        super(FundsReconciliationRecordRecordDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationSummary().merchantAccount().pmc().name())).customLabel("PMC:")
                .build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationSummary().reconciliationFile())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationSummary().reconciliationFile().fundsTransferType())).build());

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().merchantTerminalId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().paymentDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().clientId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().transactionId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().amount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationStatus())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reasonCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reasonText())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fee())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processingStatus())).build());

        panel.setH1(++row, 0, 2, i18n.tr("Funds Transfer Record"));

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().bankId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().branchTransitNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().accountNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().transactionId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().acknowledgmentStatusCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().processed())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecord().processingStatus())).build());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
