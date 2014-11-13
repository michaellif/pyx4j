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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadSimDebitRecordFolder;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;

public class PadSimBatchForm extends OperationsEntityForm<PadSimBatch> {

    private static final I18n i18n = I18n.get(PadSimBatchForm.class);

    public PadSimBatchForm(IFormView<PadSimBatch> view) {
        super(PadSimBatch.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Batch Details"));

        formPanel.append(Location.Left, proto().padFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class)))
                .decorate().componentWidth(420);

        formPanel.append(Location.Right, proto().batchNumber()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().terminalId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().bankId()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().branchTransitNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().accountNumber()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().chargeDescription()).decorate().componentWidth(300);
        formPanel.append(Location.Left, proto().batchAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().recordsCount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().updated()).decorate();

        formPanel.h1(i18n.tr("Acknowledgment"));

        formPanel.append(Location.Left, proto().acknowledgmentStatusCode()).decorate().componentWidth(120);

        formPanel.h1(i18n.tr("Reconciliation"));

        formPanel.append(Location.Left, proto().reconciliationStatus()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().grossPaymentAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().grossPaymentFee()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().grossPaymentCount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().rejectItemsAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().rejectItemsFee()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().rejectItemsCount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().returnItemsAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().returnItemsFee()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().returnItemsCount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().netAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().adjustments()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().previousBalance()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().merchantBalance()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().fundsReleased()).decorate().componentWidth(120);

        formPanel.h1(i18n.tr("Detail Debit Records"));
        formPanel.append(Location.Dual, proto().records(), new PadSimDebitRecordFolder(isEditable()));

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}