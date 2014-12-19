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
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationMerchantAccountCrudService;

abstract class CardServiceSimulationMerchantAccountSelectorDialog extends EntitySelectorTableDialog<CardServiceSimulationMerchantAccount> {

    public CardServiceSimulationMerchantAccountSelectorDialog() {
        super(CardServiceSimulationMerchantAccount.class, false, "Select MerchantAccount");
        setDialogPixelWidth(700);
        getLister().setHeight("400px");
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//
                new ColumnDescriptor.Builder(proto().terminalID()).build(), //
                new ColumnDescriptor.Builder(proto().company()).build()//
                );
    }

    @Override
    protected AbstractListCrudService<CardServiceSimulationMerchantAccount> getSelectService() {
        return GWT.<AbstractCrudService<CardServiceSimulationMerchantAccount>> create(CardServiceSimulationMerchantAccountCrudService.class);
    }

}
