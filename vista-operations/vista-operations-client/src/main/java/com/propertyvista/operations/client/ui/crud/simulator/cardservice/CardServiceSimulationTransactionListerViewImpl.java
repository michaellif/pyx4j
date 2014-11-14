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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionListerViewImpl extends AbstractListerView<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionListerView {

    public static class CardServiceSimulationTransactionLister extends SiteDataTablePanel<CardServiceSimulationTransaction> {

        public CardServiceSimulationTransactionLister() {
            super(CardServiceSimulationTransaction.class, GWT
                    .<AbstractCrudService<CardServiceSimulationTransaction>> create(CardServiceSimulationTransactionCrudService.class), false, true);
            setDataTableModel(new DataTableModel<CardServiceSimulationTransaction>( //
                    new MemberColumnDescriptor.Builder(proto().id()).build(), //
                    new MemberColumnDescriptor.Builder(proto().card().cardNumber()).columnTitle("Card Number").build(), //
                    new MemberColumnDescriptor.Builder(proto().merchant()).build(), //
                    new MemberColumnDescriptor.Builder(proto().merchant().company()).build(), //
                    new MemberColumnDescriptor.Builder(proto().transactionType()).build(), //
                    new MemberColumnDescriptor.Builder(proto().amount()).build(), //
                    new MemberColumnDescriptor.Builder(proto().convenienceFee()).build(), //
                    new MemberColumnDescriptor.Builder(proto().reference()).build(), //
                    new MemberColumnDescriptor.Builder(proto().responseCode()).build(), //
                    new MemberColumnDescriptor.Builder(proto().authorizationNumber()).build(), //
                    new MemberColumnDescriptor.Builder(proto().voided()).build(), //
                    new MemberColumnDescriptor.Builder(proto().transactionDate()).build() //
            ));
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().id(), true));
        }
    }

    public CardServiceSimulationTransactionListerViewImpl() {
        setDataTablePanel(new CardServiceSimulationTransactionLister());
    }

}
