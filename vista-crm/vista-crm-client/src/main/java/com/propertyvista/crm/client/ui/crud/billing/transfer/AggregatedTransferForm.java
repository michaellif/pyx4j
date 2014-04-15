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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferForm extends CrmEntityForm<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferForm.class);

    public AggregatedTransferForm(IForm<AggregatedTransfer> view) {
        super(AggregatedTransfer.class, view);
        createTabs();
    }

    public void createTabs() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, inject(proto().paymentDate(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().status(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().merchantAccount(), new FormDecoratorBuilder(20).build()));
        content.setWidget(++row, 0, inject(proto().fundsTransferType(), new FormDecoratorBuilder(20).build()));

        content.setWidget(++row, 0, inject(proto().netAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().adjustments(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().previousBalance(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().merchantBalance(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().fundsReleased(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().grossPaymentAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().grossPaymentFee(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().grossPaymentCount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().rejectItemsAmount(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().returnItemsAmount(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().rejectItemsFee(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().returnItemsFee(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().rejectItemsCount(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().returnItemsCount(), new FormDecoratorBuilder(5).build()));

        content.setH3(++row, 0, 1, proto().payments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getPaymentsListerView().asWidget());
        content.setH3(++row, 0, 1, proto().returnedPayments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getReturnedPaymentsListerView().asWidget());

        content.setH3(++row, 0, 1, proto().rejectedBatchPayments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getRejectedBatchPaymentsListerView().asWidget());

        selectTab(addTab(content));

    }
}