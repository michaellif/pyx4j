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
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationSummaryForm extends OperationsEntityForm<FundsReconciliationSummaryDTO> {

    public FundsReconciliationSummaryForm(IForm<FundsReconciliationSummaryDTO> view) {
        super(FundsReconciliationSummaryDTO.class, view);
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int i = 0;
        panel.setWidget(++i, 0, 2, inject(proto().paymentDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().merchantTerminalId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().reconciliationStatus(), new FieldDecoratorBuilder().build()));
        CLabel<Object> recordsCount;
        panel.setWidget(++i, 0, 2, inject(proto().recordsCount(), recordsCount = new CLabel<>(), new FieldDecoratorBuilder().build()));
        recordsCount.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace summaryRecordsPlace = new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord().formListerPlace(getValue().getPrimaryKey());
                AppSite.getPlaceController().goTo(summaryRecordsPlace);
            }
        });

        panel.setWidget(++i, 0, 2, inject(proto().grossPaymentCount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().grossPaymentAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().grossPaymentFee(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++i, 0, 2, inject(proto().rejectItemsCount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().rejectItemsAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().rejectItemsFee(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++i, 0, 2, inject(proto().returnItemsCount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().returnItemsAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().returnItemsFee(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++i, 0, 2, inject(proto().netAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().adjustments(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().previousBalance(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().merchantBalance(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++i, 0, 2, inject(proto().fundsReleased(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++i, 0, 2, inject(proto().processingStatus(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(panel));
    }
}
