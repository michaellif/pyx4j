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

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
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
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().paymentDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().status()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().merchantAccount()).decorate().componentWidth(240);
        formPanel.append(Location.Left, proto().fundsTransferType()).decorate().componentWidth(240);

        formPanel.append(Location.Left, proto().netAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().adjustments()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().previousBalance()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().merchantBalance()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().fundsReleased()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().grossPaymentAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().grossPaymentFee()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().grossPaymentCount()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().rejectItemsAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().returnItemsAmount()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().rejectItemsFee()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().returnItemsFee()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().rejectItemsCount()).decorate().componentWidth(60);
        formPanel.append(Location.Right, proto().returnItemsCount()).decorate().componentWidth(60);

        formPanel.h3(proto().payments().getMeta().getCaption());
        formPanel.append(Location.Full, ((AggregatedTransferViewerView) getParentView()).getPaymentsListerView().asWidget());
        formPanel.h3(proto().returnedPayments().getMeta().getCaption());
        formPanel.append(Location.Full, ((AggregatedTransferViewerView) getParentView()).getReturnedPaymentsListerView().asWidget());

        formPanel.h3(proto().rejectedBatchPayments().getMeta().getCaption());
        formPanel.append(Location.Full, ((AggregatedTransferViewerView) getParentView()).getRejectedBatchPaymentsListerView().asWidget());

        selectTab(addTab(formPanel, i18n.tr("General")));

    }
}