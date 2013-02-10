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
package com.propertyvista.operations.client.ui.crud.cardservicesimulation;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class CardServiceSimulationTransactionEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionEditorView {

    public static class CardServiceSimulationTransactionForm extends OperationsEntityForm<CardServiceSimulationTransaction> {

        public CardServiceSimulationTransactionForm(IFormView<CardServiceSimulationTransaction> view) {
            super(CardServiceSimulationTransaction.class, view);

            FormFlexPanel tabPanel = new FormFlexPanel("General Transaction Data");
            int row = -1;

            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().card().number())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transactionType())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reference())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().responseCode())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().authorizationNumber())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transactionDate())).build());

            selectTab(addTab(tabPanel));
        }

    }

    public CardServiceSimulationTransactionEditorViewImpl() {
        super(OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationTransaction.class);
        setForm(new CardServiceSimulationTransactionForm(this));
    }

}
