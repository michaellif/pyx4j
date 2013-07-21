/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.pad.batch;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;

public class PadBatchForm extends OperationsEntityForm<PadSimBatch> {

    private static final I18n i18n = I18n.get(PadBatchForm.class);

    public PadBatchForm(IForm<PadSimBatch> view) {
        super(PadSimBatch.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("Batch Details"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().padFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class))), 35)
                        .build());

        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().batchNumber()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().terminalId()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().bankId()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().accountNumber()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeDescription()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().batchAmount()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().recordsCount()), 10).build());

        content.setH1(++row, 0, 1, i18n.tr("Reconciliation"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().reconciliationStatus()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().grossPaymentAmount()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().grossPaymentFee()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().grossPaymentCount()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejectItemsAmount()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().rejectItemsFee()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejectItemsCount()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().returnItemsAmount()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().returnItemsFee()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().returnItemsCount()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().netAmount()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().adjustments()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().previousBalance()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().merchantBalance()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().fundsReleased()), 10).build());

        content.setH1(++row, 0, 1, i18n.tr("Detail Debit Records"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, inject(proto().records(), new DebitRecordFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        selectTab(addTab(content));
    }
}