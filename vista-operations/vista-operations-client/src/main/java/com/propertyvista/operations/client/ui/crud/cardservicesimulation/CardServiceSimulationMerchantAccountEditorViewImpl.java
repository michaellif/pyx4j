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
import com.propertyvista.operations.domain.dev.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class CardServiceSimulationMerchantAccountEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationMerchantAccount> implements
        CardServiceSimulationMerchantAccountEditorView {

    public static class CardServiceSimulationMerchantAccountForm extends OperationsEntityForm<CardServiceSimulationMerchantAccount> {

        public CardServiceSimulationMerchantAccountForm(IFormView<CardServiceSimulationMerchantAccount> view) {
            super(CardServiceSimulationMerchantAccount.class, view);

            FormFlexPanel tabPanel = new FormFlexPanel("Merchant Account");
            int row = -1;

            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().terminalID())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balance())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().responseCode())).build());
            tabPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created())).build());

            selectTab(addTab(tabPanel));
        }

    }

    public CardServiceSimulationMerchantAccountEditorViewImpl() {
        super(OperationsSiteMap.Simulation.CardServiceSimulation.CardServiceSimulationMerchantAccount.class);
        setForm(new CardServiceSimulationMerchantAccountForm(this));
    }

}
