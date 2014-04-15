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
        content.setWidget(++row, 0, injectAndDecorate(proto().paymentDate(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().status(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().merchantAccount(), 20));
        content.setWidget(++row, 0, injectAndDecorate(proto().fundsTransferType(), 20));

        content.setWidget(++row, 0, injectAndDecorate(proto().netAmount(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().adjustments(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().previousBalance(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().merchantBalance(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().fundsReleased(), 10));

        content.setWidget(++row, 0, injectAndDecorate(proto().grossPaymentAmount(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().grossPaymentFee(), 10));
        content.setWidget(++row, 0, injectAndDecorate(proto().grossPaymentCount(), 10));

        content.setWidget(++row, 0, injectAndDecorate(proto().rejectItemsAmount(), 10));
        content.setWidget(row, 1, injectAndDecorate(proto().returnItemsAmount(), 10));

        content.setWidget(++row, 0, injectAndDecorate(proto().rejectItemsFee(), 10));
        content.setWidget(row, 1, injectAndDecorate(proto().returnItemsFee(), 10));

        content.setWidget(++row, 0, injectAndDecorate(proto().rejectItemsCount(), 5));
        content.setWidget(row, 1, injectAndDecorate(proto().returnItemsCount(), 5));

        content.setH3(++row, 0, 1, proto().payments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getPaymentsListerView().asWidget());
        content.setH3(++row, 0, 1, proto().returnedPayments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getReturnedPaymentsListerView().asWidget());

        content.setH3(++row, 0, 1, proto().rejectedBatchPayments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, ((AggregatedTransferViewerView) getParentView()).getRejectedBatchPaymentsListerView().asWidget());

        selectTab(addTab(content));

    }
}