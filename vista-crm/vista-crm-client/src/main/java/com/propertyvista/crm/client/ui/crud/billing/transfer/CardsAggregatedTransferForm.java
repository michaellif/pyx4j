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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CKeyField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;

public class CardsAggregatedTransferForm extends CForm<CardsAggregatedTransfer> {

    private static final I18n i18n = I18n.get(CardsAggregatedTransferForm.class);

    private final AggregatedTransferViewerView view;

    public CardsAggregatedTransferForm(AggregatedTransferViewerView view) {
        super(CardsAggregatedTransfer.class, new VistaEditorsComponentFactory());
        this.view = view;
        init();
        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().paymentDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().status()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().merchantAccount()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().fundsTransferType()).decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().netAmount()).decorate().componentWidth(120);
//        formPanel.append(Location.Left, proto().adjustments()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().grossPaymentAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().grossPaymentFee()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().grossPaymentCount()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().cardsReconciliationRecordKey(), new CKeyField()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().visaDeposit()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().visaFee()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().mastercardDeposit()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().mastercardFee()).decorate().componentWidth(120);

        formPanel.h3(proto().payments().getMeta().getCaption());
        formPanel.append(Location.Dual, view.getPaymentsListerView().asWidget());
        formPanel.h3(proto().returnedPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, view.getReturnedPaymentsListerView().asWidget());

        formPanel.h3(proto().rejectedBatchPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, view.getRejectedBatchPaymentsListerView().asWidget());

        return formPanel;

    }
}