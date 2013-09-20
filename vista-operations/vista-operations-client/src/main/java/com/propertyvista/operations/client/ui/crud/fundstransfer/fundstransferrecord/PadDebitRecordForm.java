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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;

public class PadDebitRecordForm extends OperationsEntityForm<PadDebitRecordDTO> {

    public PadDebitRecordForm(IForm<PadDebitRecordDTO> view) {
        super(PadDebitRecordDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().pmc().name())).customLabel("PMC:").build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().merchantTerminalId())).build());

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().padFile())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().padBatch().padFile().fundsTransferType())).build());

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().clientId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().amount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().bankId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().branchTransitNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().accountNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().transactionId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatusCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processed())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().processingStatus())).build());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
