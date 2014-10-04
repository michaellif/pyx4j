/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;

public class CardServiceSimulationMerchantAccountForm extends OperationsEntityForm<CardServiceSimulationMerchantAccount> {

    public CardServiceSimulationMerchantAccountForm(IForm<CardServiceSimulationMerchantAccount> view) {
        super(CardServiceSimulationMerchantAccount.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().company()).decorate();
        formPanel.append(Location.Left, proto().terminalID()).decorate();
        formPanel.append(Location.Left, proto().balance()).decorate();
        formPanel.append(Location.Left, proto().responseCode()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();

        formPanel.h2("Convenience Fee");
        formPanel.append(Location.Left, proto().visaCreditConvenienceFee()).decorate();
        formPanel.append(Location.Left, proto().masterCardConvenienceFee()).decorate();
        formPanel.append(Location.Left, proto().visaDebitConvenienceFee()).decorate();

        formPanel.h2("Processing Fee");
        formPanel.append(Location.Left, proto().visaCreditFee()).decorate();
        formPanel.append(Location.Left, proto().masterCardFee()).decorate();
        formPanel.append(Location.Left, proto().visaDebitFee()).decorate();

        setTabBarVisible(false);
        selectTab(addTab(formPanel, "Card Service Simulation"));
    }
}