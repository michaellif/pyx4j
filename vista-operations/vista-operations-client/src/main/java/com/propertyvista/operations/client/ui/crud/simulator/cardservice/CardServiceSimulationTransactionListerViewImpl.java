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

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;

public class CardServiceSimulationTransactionListerViewImpl extends OperationsListerViewImplBase<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionListerView {

    public static class CardServiceSimulationTransactionLister extends AbstractLister<CardServiceSimulationTransaction> {

        public CardServiceSimulationTransactionLister() {
            super(CardServiceSimulationTransaction.class, false, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().id()).build(),
                    new MemberColumnDescriptor.Builder(proto().card().number()).columnTitle("Card Number").build(),
                    new MemberColumnDescriptor.Builder(proto().merchant()).build(),
                    new MemberColumnDescriptor.Builder(proto().transactionType() ).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().convenienceFee()).build(),
                    new MemberColumnDescriptor.Builder(proto().reference()).build(),
                    new MemberColumnDescriptor.Builder(proto().responseCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().authorizationNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().voided()).build(),
                    new MemberColumnDescriptor.Builder(proto().transactionDate()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().id(), true));
        }
    }

    public CardServiceSimulationTransactionListerViewImpl() {
        setLister(new CardServiceSimulationTransactionLister());
    }

}
