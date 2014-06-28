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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;

public class AggregatedTransferFormPolymorphic extends CrmEntityForm<AggregatedTransfer> {

    private static final I18n i18n = I18n.get(AggregatedTransferFormPolymorphic.class);

    private final FormPanel detailsPanel = new FormPanel(this);

    private final CardsAggregatedTransferForm cardsAggregatedTransferForm;

    private final EftAggregatedTransferForm eftAggregatedTransferForm;

    private final Tab returnedPaymentsTab;

    private final Tab rejectedBatchPaymentsTab;

    public AggregatedTransferFormPolymorphic(AggregatedTransferViewerView view) {
        super(AggregatedTransfer.class, view);

        // common data form:
        selectTab(addTab(detailsPanel, i18n.tr("Details")));

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

    @Override
    protected void onValuePropagation(AggregatedTransfer value, boolean fireEvent, boolean populate) {
        setPolimorphicData(value, fireEvent, populate);
        super.onValuePropagation(value, fireEvent, populate);
    }

    private void setPolimorphicData(AggregatedTransfer value, boolean fireEvent, boolean populate) {
        detailsPanel.clear();

        if (value != null) {
            if (value.getInstanceValueClass().equals(CardsAggregatedTransfer.class)) {

                detailsPanel.append(Location.Dual, cardsAggregatedTransferForm.asWidget());
                setTabVisible(returnedPaymentsTab, false);
                setTabVisible(rejectedBatchPaymentsTab, false);

            } else if (value.getInstanceValueClass().equals(EftAggregatedTransfer.class)) {

                detailsPanel.append(Location.Dual, eftAggregatedTransferForm.asWidget());
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

        if (getValue().getInstanceValueClass().equals(CardsAggregatedTransfer.class)) {
            cardsAggregatedTransferForm.setValue((CardsAggregatedTransfer) getValue(), !populate, populate);
        } else if (getValue().getInstanceValueClass().equals(EftAggregatedTransfer.class)) {
            eftAggregatedTransferForm.setValue((EftAggregatedTransfer) getValue(), !populate, populate);
        } else {
            assert false;
        }
    }

    @Override
    public void onReset() {
        super.onReset();

        cardsAggregatedTransferForm.reset();
        eftAggregatedTransferForm.reset();
    }
}