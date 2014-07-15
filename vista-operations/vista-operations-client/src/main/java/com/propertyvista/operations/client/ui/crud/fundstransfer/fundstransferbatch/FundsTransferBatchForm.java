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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;

public class FundsTransferBatchForm extends OperationsEntityForm<FundsTransferBatchDTO> {

    private static final I18n i18n = I18n.get(FundsTransferBatchForm.class);

    public FundsTransferBatchForm(IForm<FundsTransferBatchDTO> view) {
        super(FundsTransferBatchDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().batchNumber()).decorate();

        formPanel.append(Location.Left, proto().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().merchantTerminalId()).decorate();

        formPanel.append(Location.Left, proto().padFile(),
                new CEntityCrudHyperlink<FundsTransferFile>(AppPlaceEntityMapper.resolvePlace(FundsTransferFileDTO.class))).decorate();

        formPanel.append(Location.Left, proto().bankId()).decorate();
        formPanel.append(Location.Left, proto().branchTransitNumber()).decorate();
        formPanel.append(Location.Left, proto().accountNumber()).decorate();
        formPanel.append(Location.Left, proto().chargeDescription()).decorate();
        formPanel.append(Location.Left, proto().batchAmount()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentStatusCode()).decorate();
        formPanel.append(Location.Left, proto().processingStatus()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
