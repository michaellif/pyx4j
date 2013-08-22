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
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;

public class CardServiceSimulationTransactionEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionEditorView {

    public static class CardServiceSimulationTransactionForm extends OperationsEntityForm<CardServiceSimulationTransaction> {

        public CardServiceSimulationTransactionForm(IForm<CardServiceSimulationTransaction> view) {
            super(CardServiceSimulationTransaction.class, view);

            TwoColumnFlexFormPanel tabPanel = new TwoColumnFlexFormPanel("General Transaction Data");
            int row = -1;

            tabPanel.setWidget(++row, 0,
                    new FormDecoratorBuilder(inject(proto().card(), OperationsEditorsComponentFactory.createEntityHyperlink(CardServiceSimulationCard.class)),
                            18).build());

            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().transactionType())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().scheduledSimulatedResponce())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reference())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().responseCode())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().authorizationNumber())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().voided())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().transactionDate())).build());

            selectTab(addTab(tabPanel));
        }
    }

    public CardServiceSimulationTransactionEditorViewImpl() {
        setForm(new CardServiceSimulationTransactionForm(this));
    }

}
