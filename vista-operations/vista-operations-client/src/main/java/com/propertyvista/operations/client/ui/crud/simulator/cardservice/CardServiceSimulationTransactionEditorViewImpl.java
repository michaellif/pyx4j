/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;

public class CardServiceSimulationTransactionEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionEditorView {

    public static class CardServiceSimulationTransactionForm extends OperationsEntityForm<CardServiceSimulationTransaction> {

        public CardServiceSimulationTransactionForm(IForm<CardServiceSimulationTransaction> view) {
            super(CardServiceSimulationTransaction.class, view);

            FormPanel formPanel = new FormPanel(this);

            formPanel.h1("Card Service Simulation");
            formPanel.append(Location.Left, proto().card(), OperationsEditorsComponentFactory.createEntityHyperlink(CardServiceSimulationCard.class))
                    .decorate().componentWidth(216);

            formPanel
                    .append(Location.Left, proto().merchant(),
                            OperationsEditorsComponentFactory.createEntityHyperlink(CardServiceSimulationMerchantAccount.class)).decorate().componentWidth(216);

            formPanel.append(Location.Left, proto().transactionType()).decorate();
            formPanel.append(Location.Left, proto().scheduledSimulatedResponce()).decorate();
            formPanel.append(Location.Left, proto().amount()).decorate();
            formPanel.append(Location.Left, proto().convenienceFee()).decorate();
            formPanel.append(Location.Left, proto().reference()).decorate();
            formPanel.append(Location.Left, proto().responseCode()).decorate();
            formPanel.append(Location.Left, proto().authorizationNumber()).decorate();
            formPanel.append(Location.Left, proto().voided()).decorate();
            formPanel.append(Location.Left, proto().transactionDate()).decorate();

            selectTab(addTab(formPanel, "Card Service Simulation"));
            setTabBarVisible(false);
        }
    }

    public CardServiceSimulationTransactionEditorViewImpl() {
        setForm(new CardServiceSimulationTransactionForm(this));
    }

}
