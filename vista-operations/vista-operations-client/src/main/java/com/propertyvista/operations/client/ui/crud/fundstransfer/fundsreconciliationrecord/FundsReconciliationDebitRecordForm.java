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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.backoffice.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationDebitRecordForm extends OperationsEntityForm<FundsReconciliationRecordRecordDTO> {

    private static final I18n i18n = I18n.get(FundsReconciliationDebitRecordForm.class);

    public FundsReconciliationDebitRecordForm(IForm<FundsReconciliationRecordRecordDTO> view) {
        super(FundsReconciliationRecordRecordDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().reconciliationSummary().merchantAccount().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().reconciliationSummary().reconciliationFile(),
                new CEntityCrudHyperlink<FundsReconciliationFile>(AppPlaceEntityMapper.resolvePlace(FundsReconciliationFileDTO.class))).decorate();

        formPanel.append(Location.Left, proto().reconciliationSummary().reconciliationFile().fundsTransferType()).decorate();

        formPanel.append(Location.Left, proto().merchantTerminalId()).decorate();

        formPanel.append(Location.Left, proto().reconciliationSummary(),
                new CEntityCrudHyperlink<FundsReconciliationSummary>(AppPlaceEntityMapper.resolvePlace(FundsReconciliationSummaryDTO.class))).decorate();
        formPanel.append(Location.Left, proto().paymentDate()).decorate();
        formPanel.append(Location.Left, proto().clientId()).decorate();
        formPanel.append(Location.Left, proto().transactionId()).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate();
        formPanel.append(Location.Left, proto().reconciliationStatus()).decorate();
        formPanel.append(Location.Left, proto().reasonCode()).decorate();
        formPanel.append(Location.Left, proto().reasonText()).decorate();
        formPanel.append(Location.Left, proto().fee()).decorate();
        formPanel.append(Location.Left, proto().processingStatus()).decorate();

        formPanel.h1(i18n.tr("Funds Transfer Record"));

        formPanel.append(Location.Left, proto().debitRecord().bankId()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().branchTransitNumber()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().accountNumber()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().transactionId()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().acknowledgmentStatusCode()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().processed()).decorate();
        formPanel.append(Location.Left, proto().debitRecord().processingStatus()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
