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
import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.dev.CardServiceSimulationMerchantAccount;

public class CardServiceSimulationMerchantAccountEditorViewImpl extends OperationsEditorViewImplBase<CardServiceSimulationMerchantAccount> implements
        CardServiceSimulationMerchantAccountEditorView {

    public static class CardServiceSimulationMerchantAccountForm extends OperationsEntityForm<CardServiceSimulationMerchantAccount> {

        public CardServiceSimulationMerchantAccountForm(IForm<CardServiceSimulationMerchantAccount> view) {
            super(CardServiceSimulationMerchantAccount.class, view);

            TwoColumnFlexFormPanel tabPanel = new TwoColumnFlexFormPanel();
            int row = -1;

            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().terminalID())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().balance())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().responseCode())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().created())).build());

            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().visaCreditConvenienceFee())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().masterCardConvenienceFee())).build());
            tabPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().visaDebitConvenienceFee())).build());

            setTabBarVisible(false);
            selectTab(addTab(tabPanel));
        }

    }

    public CardServiceSimulationMerchantAccountEditorViewImpl() {
        setForm(new CardServiceSimulationMerchantAccountForm(this));
    }

}
