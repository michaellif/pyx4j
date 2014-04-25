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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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

            TwoColumnFlexFormPanel tabPanel = new TwoColumnFlexFormPanel();
            int row = -1;

            tabPanel.setWidget(++row, 0, inject(proto().card(), // 
                    OperationsEditorsComponentFactory.createEntityHyperlink(CardServiceSimulationCard.class), new FieldDecoratorBuilder(18).build()));

            tabPanel.setWidget(++row, 0, inject(proto().merchant(), //
                    OperationsEditorsComponentFactory.createEntityHyperlink(CardServiceSimulationMerchantAccount.class), new FieldDecoratorBuilder(18).build()));

            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().transactionType()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().scheduledSimulatedResponce()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().amount()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().convenienceFee()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().reference()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().responseCode()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().authorizationNumber()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().voided()));
            tabPanel.setWidget(++row, 0, injectAndDecorate(proto().transactionDate()));

            selectTab(addTab(tabPanel, "Card Service Simulation"));
        }
    }

    public CardServiceSimulationTransactionEditorViewImpl() {
        setForm(new CardServiceSimulationTransactionForm(this));
    }

}
