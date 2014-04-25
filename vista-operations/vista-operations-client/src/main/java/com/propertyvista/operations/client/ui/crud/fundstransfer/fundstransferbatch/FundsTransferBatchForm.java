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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferbatch;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;

public class FundsTransferBatchForm extends OperationsEntityForm<FundsTransferBatchDTO> {

    private static final I18n i18n = I18n.get(FundsTransferBatchForm.class);

    public FundsTransferBatchForm(IForm<FundsTransferBatchDTO> view) {
        super(FundsTransferBatchDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().batchNumber(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().pmc().name(), new FieldDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().merchantTerminalId(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().bankId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().branchTransitNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().accountNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().chargeDescription(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().batchAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().acknowledgmentStatusCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().processingStatus(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(panel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
