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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;

public class FundsTransferDebitRecordForm extends OperationsEntityForm<FundsTransferRecordDTO> {

    private static final I18n i18n = I18n.get(FundsTransferDebitRecordForm.class);

    public FundsTransferDebitRecordForm(IFormView<FundsTransferRecordDTO> view) {
        super(FundsTransferRecordDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().padBatch().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().padBatch().merchantTerminalId()).decorate();

        formPanel.append(Location.Left, proto().padBatch().padFile(),
                new CEntityCrudHyperlink<FundsTransferFile>(AppPlaceEntityMapper.resolvePlace(FundsTransferFileDTO.class))).decorate();

        formPanel.append(Location.Left, proto().padBatch().padFile().fundsTransferType()).decorate();
        formPanel.append(Location.Left, proto().padBatch().padFile().sent()).decorate();

        formPanel.append(Location.Left, proto().padBatch(),
                new CEntityCrudHyperlink<FundsTransferBatch>(AppPlaceEntityMapper.resolvePlace(FundsTransferBatchDTO.class))).decorate();

        formPanel.append(Location.Left, proto().clientId()).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate();
        formPanel.append(Location.Left, proto().bankId()).decorate();
        formPanel.append(Location.Left, proto().branchTransitNumber()).decorate();
        formPanel.append(Location.Left, proto().accountNumber()).decorate();
        formPanel.append(Location.Left, proto().transactionId()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentStatusCode()).decorate();
        formPanel.append(Location.Left, proto().processed()).decorate();
        formPanel.append(Location.Left, proto().processingStatus()).decorate();
        formPanel.append(Location.Left, proto().statusChangeDate()).decorate();

        formPanel.h1(i18n.tr("Reconciliation Record Paid Or Rejected"));

        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().paymentDate()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().reconciliationStatus()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().reasonCode()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().reasonText()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().fee()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordPaidOrRejected().processingStatus()).decorate();

        formPanel.h1(i18n.tr("Reconciliation Record Return"));

        formPanel.append(Location.Left, proto().reconciliationRecordReturn().paymentDate()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordReturn().reconciliationStatus()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordReturn().reasonCode()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordReturn().reasonText()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordReturn().fee()).decorate();
        formPanel.append(Location.Left, proto().reconciliationRecordReturn().processingStatus()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
