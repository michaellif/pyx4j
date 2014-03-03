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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationSummaryForm extends OperationsEntityForm<FundsReconciliationSummaryDTO> {

    public FundsReconciliationSummaryForm(IForm<FundsReconciliationSummaryDTO> view) {
        super(FundsReconciliationSummaryDTO.class, view);
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int i = 0;
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().paymentDate())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().merchantTerminalId())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().reconciliationStatus())).build());
        CLabel<Object> recordsCount;
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().recordsCount(), recordsCount = new CLabel<>())).build());
        recordsCount.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace summaryRecordsPlace = new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord().formListerPlace(getValue().getPrimaryKey());
                AppSite.getPlaceController().goTo(summaryRecordsPlace);
            }
        });

        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().grossPaymentCount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().grossPaymentAmount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().grossPaymentFee())).build());

        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().rejectItemsCount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().rejectItemsAmount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().rejectItemsFee())).build());

        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().returnItemsCount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().returnItemsAmount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().returnItemsFee())).build());

        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().netAmount())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().adjustments())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().previousBalance())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().merchantBalance())).build());
        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().fundsReleased())).build());

        panel.setWidget(++i, 0, 2, new FormDecoratorBuilder(inject(proto().processingStatus())).build());

        selectTab(addTab(panel));
    }

}
