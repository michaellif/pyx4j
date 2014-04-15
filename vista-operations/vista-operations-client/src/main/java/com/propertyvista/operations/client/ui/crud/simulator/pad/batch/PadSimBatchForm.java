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

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;

public class PadSimBatchForm extends OperationsEntityForm<PadSimBatch> {

    private static final I18n i18n = I18n.get(PadSimBatchForm.class);

    public PadSimBatchForm(IForm<PadSimBatch> view) {
        super(PadSimBatch.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("Batch Details"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(
                ++row,
                0,
                inject(proto().padFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class)), new FormDecoratorBuilder(
                        35).build()));

        content.setWidget(row, 1, inject(proto().batchNumber(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().terminalId(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().bankId(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().branchTransitNumber(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().accountNumber(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().chargeDescription(), new FormDecoratorBuilder(25).build()));
        content.setWidget(++row, 0, inject(proto().batchAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().recordsCount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().updated(), new FormDecoratorBuilder().build()));

        content.setH1(++row, 0, 1, i18n.tr("Acknowledgment"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, inject(proto().acknowledgmentStatusCode(), new FormDecoratorBuilder(10).build()));

        content.setH1(++row, 0, 1, i18n.tr("Reconciliation"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, inject(proto().reconciliationStatus(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().grossPaymentAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().grossPaymentFee(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().grossPaymentCount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().rejectItemsAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().rejectItemsFee(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().rejectItemsCount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().returnItemsAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().returnItemsFee(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().returnItemsCount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().netAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().adjustments(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().previousBalance(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().merchantBalance(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().fundsReleased(), new FormDecoratorBuilder(10).build()));

        content.setH1(++row, 0, 1, i18n.tr("Detail Debit Records"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, inject(proto().records(), new DebitRecordFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        selectTab(addTab(content));
    }
}