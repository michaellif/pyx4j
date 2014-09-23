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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.CardsAggregatedTransferDTO;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransferAdjustment;
import com.propertyvista.domain.financial.AggregatedTransferChargeback;
import com.propertyvista.domain.financial.AggregatedTransferNonVistaTransaction;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;

public class AggregatedTransferForm extends CrmEntityForm<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferForm.class);

    private final FormPanel polyDataPanel = new FormPanel(this);

    private final CardsAggregatedTransferForm cardsAggregatedTransferForm;

    private final EftAggregatedTransferForm eftAggregatedTransferForm;

    private final Tab returnedPaymentsTab;

    private final Tab rejectedBatchPaymentsTab;

    private Widget nonVistaTransactionsLabel;

    public AggregatedTransferForm(AggregatedTransferViewerView view) {
        super(AggregatedTransfer.class, view);

        // common data form:
        selectTab(addTab(createGeneralPanel(), i18n.tr("General")));
        selectTab(addTab(polyDataPanel, i18n.tr("Details")));

        FormPanel tabPanel = new FormPanel(this);
        tabPanel.append(Location.Dual, view.getPaymentsListerView().asWidget());
        addTab(tabPanel, proto().payments().getMeta().getCaption());

        tabPanel = new FormPanel(this);
        tabPanel.append(Location.Dual, view.getReturnedPaymentsListerView().asWidget());
        returnedPaymentsTab = addTab(tabPanel, proto().returnedPayments().getMeta().getCaption());

        tabPanel = new FormPanel(this);
        tabPanel.append(Location.Dual, view.getRejectedBatchPaymentsListerView().asWidget());
        rejectedBatchPaymentsTab = addTab(tabPanel, proto().rejectedBatchPayments().getMeta().getCaption());

        // poly data forms:
        cardsAggregatedTransferForm = new CardsAggregatedTransferForm(view);
        eftAggregatedTransferForm = new EftAggregatedTransferForm(view);
    }

    private FormPanel createGeneralPanel() {
        FormPanel tabPanel = new FormPanel(this);

        tabPanel.append(Location.Left, proto().paymentDate()).decorate().componentWidth(120);
        tabPanel.append(Location.Left, proto().status()).decorate().componentWidth(120);
        tabPanel.append(Location.Left, proto().merchantAccount()).decorate().componentWidth(200);
        tabPanel.append(Location.Left, proto().fundsTransferType()).decorate().componentWidth(200);

        tabPanel.append(Location.Right, proto().netAmount()).decorate().componentWidth(120);
        tabPanel.append(Location.Right, proto().grossPaymentAmount()).decorate().componentWidth(120);
        tabPanel.append(Location.Right, proto().grossPaymentFee()).decorate().componentWidth(120);
        tabPanel.append(Location.Right, proto().grossPaymentCount()).decorate().componentWidth(120);

        tabPanel.append(Location.Dual, proto().transactionErrorMessage()).decorate().componentWidth(120);

        tabPanel.h3(proto().adjustments().getMeta().getCaption());
        tabPanel.append(Location.Dual, proto().adjustments(), new AggregatedTransferAdjustmentFolder());

        tabPanel.h3(proto().chargebacks().getMeta().getCaption());
        tabPanel.append(Location.Dual, proto().chargebacks(), new AggregatedTransferChargebackFolder());

        nonVistaTransactionsLabel = tabPanel.h3(proto().nonVistaTransactions().getMeta().getCaption());
        tabPanel.append(Location.Dual, proto().nonVistaTransactions(), new AggregatedTransferNonVistaTransactionsFolder());

        return tabPanel;
    }

    @Override
    protected void onValuePropagation(AggregatedTransfer value, boolean fireEvent, boolean populate) {
        setPolymorphicData(value, fireEvent, populate);
        super.onValuePropagation(value, fireEvent, populate);
    }

    private void setPolymorphicData(AggregatedTransfer value, boolean fireEvent, boolean populate) {
        polyDataPanel.clear();

        if (value != null) {
            if (value.isInstanceOf(CardsAggregatedTransfer.class)) {

                polyDataPanel.append(Location.Dual, cardsAggregatedTransferForm.asWidget());
                setTabVisible(returnedPaymentsTab, false);
                setTabVisible(rejectedBatchPaymentsTab, false);

            } else if (value.isInstanceOf(EftAggregatedTransfer.class)) {

                polyDataPanel.append(Location.Dual, eftAggregatedTransferForm.asWidget());
                setTabVisible(returnedPaymentsTab, true);
                setTabVisible(rejectedBatchPaymentsTab, true);

            } else {
                assert false;
            }
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().transactionErrorMessage()).setVisible(!getValue().transactionErrorMessage().isNull());

        if (getValue().isInstanceOf(CardsAggregatedTransferDTO.class)) {
            cardsAggregatedTransferForm.setValue((CardsAggregatedTransferDTO) getValue(), !populate, populate);
        } else if (getValue().isInstanceOf(EftAggregatedTransfer.class)) {
            eftAggregatedTransferForm.setValue((EftAggregatedTransfer) getValue(), !populate, populate);
        } else {
            assert false;
        }

        nonVistaTransactionsLabel.setVisible(!getValue().nonVistaTransactions().isEmpty());
        get(proto().nonVistaTransactions()).setVisible(!getValue().nonVistaTransactions().isEmpty());
    }

    @Override
    public void onReset() {
        super.onReset();

        cardsAggregatedTransferForm.reset();
        eftAggregatedTransferForm.reset();

        nonVistaTransactionsLabel.setVisible(false);
        get(proto().nonVistaTransactions()).setVisible(false);
    }

    private class AggregatedTransferAdjustmentFolder extends VistaTableFolder<AggregatedTransferAdjustment> {

        public AggregatedTransferAdjustmentFolder() {
            super(AggregatedTransferAdjustment.class, AggregatedTransferForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().adjustment(), "15em"));
            return columns;
        }
    }

    private class AggregatedTransferChargebackFolder extends VistaTableFolder<AggregatedTransferChargeback> {

        public AggregatedTransferChargebackFolder() {
            super(AggregatedTransferChargeback.class, AggregatedTransferForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().chargeback(), "15em"));
            return columns;
        }
    }

    private class AggregatedTransferNonVistaTransactionsFolder extends VistaTableFolder<AggregatedTransferNonVistaTransaction> {

        public AggregatedTransferNonVistaTransactionsFolder() {
            super(AggregatedTransferNonVistaTransaction.class, AggregatedTransferForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().amount(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().details(), "15em"));
            return columns;
        }
    }

}