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
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferForm extends CrmEntityForm<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferForm.class);

    public AggregatedTransferForm(IForm<AggregatedTransfer> view) {
        super(AggregatedTransfer.class, view);
        createTabs();
    }

    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentDate()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantAccount()), 20).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().netAmount()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().adjustments()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().previousBalance()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().merchantBalance()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().fundsReleased()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().grossPaymentAmount()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().grossPaymentFee()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().grossPaymentCount()), 10).build());

        FormFlexPanel amount = new FormFlexPanel();
        int row2 = -1;
        amount.setWidget(++row2, 0, new FormDecoratorBuilder(inject(proto().rejectItemsAmount()), 10).build());
        amount.setWidget(++row2, 0, new FormDecoratorBuilder(inject(proto().rejectItemsFee()), 10).build());
        amount.setWidget(++row2, 0, new FormDecoratorBuilder(inject(proto().rejectItemsCount()), 5).build());
        row2 = -1;
        amount.setWidget(++row2, 1, new FormDecoratorBuilder(inject(proto().returnItemsAmount()), 10).build());
        amount.setWidget(++row2, 1, new FormDecoratorBuilder(inject(proto().returnItemsFee()), 10).build());
        amount.setWidget(++row2, 1, new FormDecoratorBuilder(inject(proto().returnItemsCount()), 5).build());

        content.setWidget(++row, 0, amount);

        content.setH3(++row, 0, 1, proto().payments().getMeta().getCaption());
        content.setWidget(++row, 0, ((AggregatedTransferViewerView) getParentView()).getPaymentsListerView().asWidget());
        content.setH3(++row, 0, 1, proto().returnedPayments().getMeta().getCaption());
        content.setWidget(++row, 0, ((AggregatedTransferViewerView) getParentView()).getReturnedPaymentsListerView().asWidget());

        content.setH3(++row, 0, 1, proto().rejectedBatchPayments().getMeta().getCaption());
        content.setWidget(++row, 0, ((AggregatedTransferViewerView) getParentView()).getRejectedBatchPaymentsListerView().asWidget());

        selectTab(addTab(content));

    }
}