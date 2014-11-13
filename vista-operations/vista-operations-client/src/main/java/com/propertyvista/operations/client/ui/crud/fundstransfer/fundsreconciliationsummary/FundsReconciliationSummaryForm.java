/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationsummary;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationSummaryForm extends OperationsEntityForm<FundsReconciliationSummaryDTO> {

    private static final I18n i18n = I18n.get(FundsReconciliationSummaryForm.class);

    public FundsReconciliationSummaryForm(IFormView<FundsReconciliationSummaryDTO> view) {
        super(FundsReconciliationSummaryDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().merchantAccount().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().reconciliationFile(),
                new CEntityCrudHyperlink<FundsReconciliationFile>(AppPlaceEntityMapper.resolvePlace(FundsReconciliationFileDTO.class))).decorate();

        formPanel.append(Location.Left, proto().reconciliationFile().fundsTransferType()).decorate();

        formPanel.append(Location.Dual, proto().paymentDate()).decorate();
        formPanel.append(Location.Dual, proto().merchantTerminalId()).decorate();
        formPanel.append(Location.Dual, proto().reconciliationStatus()).decorate();
        CLabel<Object> recordsCount;
        formPanel.append(Location.Dual, proto().recordsCount(), recordsCount = new CLabel<>()).decorate();
        recordsCount.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace summaryRecordsPlace = new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord().formListerPlace(getValue().getPrimaryKey());
                AppSite.getPlaceController().goTo(summaryRecordsPlace);
            }
        });

        formPanel.append(Location.Dual, proto().grossPaymentCount()).decorate();
        formPanel.append(Location.Dual, proto().grossPaymentAmount()).decorate();
        formPanel.append(Location.Dual, proto().grossPaymentFee()).decorate();

        formPanel.append(Location.Dual, proto().rejectItemsCount()).decorate();
        formPanel.append(Location.Dual, proto().rejectItemsAmount()).decorate();
        formPanel.append(Location.Dual, proto().rejectItemsFee()).decorate();

        formPanel.append(Location.Dual, proto().returnItemsCount()).decorate();
        formPanel.append(Location.Dual, proto().returnItemsAmount()).decorate();
        formPanel.append(Location.Dual, proto().returnItemsFee()).decorate();

        formPanel.append(Location.Dual, proto().netAmount()).decorate();

        // TODO this is list now
        //panel.setWidget(++row, 0, 2, inject(proto().adjustments(), new FieldDecoratorBuilder().build()));

        formPanel.append(Location.Dual, proto().previousBalance()).decorate();
        formPanel.append(Location.Dual, proto().merchantBalance()).decorate();
        formPanel.append(Location.Dual, proto().fundsReleased()).decorate();

        formPanel.append(Location.Dual, proto().processingStatus()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}
