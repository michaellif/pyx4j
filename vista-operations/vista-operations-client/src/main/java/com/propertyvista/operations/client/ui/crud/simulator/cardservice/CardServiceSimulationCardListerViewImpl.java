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
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationCardCrudService;

public class CardServiceSimulationCardListerViewImpl extends OperationsListerViewImplBase<CardServiceSimulationCard> implements
        CardServiceSimulationCardListerView {

    public static class CardServiceSimulationLister extends SiteDataTablePanel<CardServiceSimulationCard> {

        public CardServiceSimulationLister() {
            super(CardServiceSimulationCard.class, GWT.<AbstractCrudService<CardServiceSimulationCard>> create(CardServiceSimulationCardCrudService.class),
                    true, true);
            setDataTableModel(new DataTableModel<CardServiceSimulationCard>( //
                    new MemberColumnDescriptor.Builder(proto().cardType()).build(), //
                    new MemberColumnDescriptor.Builder(proto().cardNumber()).build(), //
                    new MemberColumnDescriptor.Builder(proto().expiryDate()).build(), //
                    new MemberColumnDescriptor.Builder(proto().balance()).build(), //
                    new MemberColumnDescriptor.Builder(proto().creditLimit(), false).build(), //
                    new MemberColumnDescriptor.Builder(proto().responseCode()).build(), //
                    new MemberColumnDescriptor.Builder(proto().created()).build() //
            ));
        }
    }

    public CardServiceSimulationCardListerViewImpl() {
        setDataTablePanel(new CardServiceSimulationLister());
    }

}
