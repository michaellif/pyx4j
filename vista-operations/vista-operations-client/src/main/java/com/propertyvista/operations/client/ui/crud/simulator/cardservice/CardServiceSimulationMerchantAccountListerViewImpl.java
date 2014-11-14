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

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationMerchantAccountCrudService;

public class CardServiceSimulationMerchantAccountListerViewImpl extends OperationsListerViewImplBase<CardServiceSimulationMerchantAccount> implements
        CardServiceSimulationMerchantAccountListerView {

    public static class CardServiceSimulationLister extends SiteDataTablePanel<CardServiceSimulationMerchantAccount> {

        public CardServiceSimulationLister() {
            super(CardServiceSimulationMerchantAccount.class, GWT
                    .<AbstractCrudService<CardServiceSimulationMerchantAccount>> create(CardServiceSimulationMerchantAccountCrudService.class), true, true);
            setDataTableModel(new DataTableModel<CardServiceSimulationMerchantAccount>( //
                    new MemberColumnDescriptor.Builder(proto().terminalID()).build(), //
                    new MemberColumnDescriptor.Builder(proto().company()).build(), //
                    new MemberColumnDescriptor.Builder(proto().balance()).build(), //
                    new MemberColumnDescriptor.Builder(proto().responseCode()).build(), //
                    new MemberColumnDescriptor.Builder(proto().created()).build() //
            ));
        }
    }

    public CardServiceSimulationMerchantAccountListerViewImpl() {
        setDataTablePanel(new CardServiceSimulationLister());
    }

}
