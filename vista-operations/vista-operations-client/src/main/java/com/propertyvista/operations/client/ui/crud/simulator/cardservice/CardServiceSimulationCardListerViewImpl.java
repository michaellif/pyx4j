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
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationCardCrudService;

public class CardServiceSimulationCardListerViewImpl extends AbstractListerView<CardServiceSimulationCard> implements CardServiceSimulationCardListerView {

    public static class CardServiceSimulationLister extends SiteDataTablePanel<CardServiceSimulationCard> {

        public CardServiceSimulationLister() {
            super(CardServiceSimulationCard.class, GWT.<AbstractCrudService<CardServiceSimulationCard>> create(CardServiceSimulationCardCrudService.class),
                    true, true);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().cardType()).build(), //
                    new ColumnDescriptor.Builder(proto().cardNumber()).build(), //
                    new ColumnDescriptor.Builder(proto().expiryDate()).build(), //
                    new ColumnDescriptor.Builder(proto().balance()).build(), //
                    new ColumnDescriptor.Builder(proto().creditLimit(), false).build(), //
                    new ColumnDescriptor.Builder(proto().responseCode()).build(), //
                    new ColumnDescriptor.Builder(proto().created()).build() //
            );

            setDataTableModel(new DataTableModel<CardServiceSimulationCard>());
        }
    }

    public CardServiceSimulationCardListerViewImpl() {
        setDataTablePanel(new CardServiceSimulationLister());
    }

}
