/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCrudService;

public class CardServiceSimulationReconciliationListerViewImpl extends AbstractListerView<CardServiceSimulationReconciliationRecord> implements
        CardServiceSimulationReconciliationListerView {

    public static class CardServiceSimulationReconciliationLister extends SiteDataTablePanel<CardServiceSimulationReconciliationRecord> {

        public CardServiceSimulationReconciliationLister() {
            super(CardServiceSimulationReconciliationRecord.class, GWT
                    .<AbstractCrudService<CardServiceSimulationReconciliationRecord>> create(CardServiceSimulationReconciliationCrudService.class), false, true);
            setDataTableModel(new DataTableModel<CardServiceSimulationReconciliationRecord>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().id()).build(),
                    new MemberColumnDescriptor.Builder(proto().fileId()).build(),
                    new MemberColumnDescriptor.Builder(proto().created()).build(),
                    new MemberColumnDescriptor.Builder(proto().depositDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().merchant()).build(),
                    new MemberColumnDescriptor.Builder(proto().merchant().company()).build(),
                    new MemberColumnDescriptor.Builder(proto().totalDeposit()).build(),
                    new MemberColumnDescriptor.Builder(proto().visaDeposit()).build(),
                    new MemberColumnDescriptor.Builder(proto().visaFee()).build(),
                    new MemberColumnDescriptor.Builder(proto().visaTransactions()).build(),
                    new MemberColumnDescriptor.Builder(proto().mastercardDeposit()).build(),
                    new MemberColumnDescriptor.Builder(proto().mastercardFee()).build(),
                    new MemberColumnDescriptor.Builder(proto().mastercardTransactions()).build()
            ));//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().id(), true));
        }
    }

    public CardServiceSimulationReconciliationListerViewImpl() {
        setDataTablePanel(new CardServiceSimulationReconciliationLister());

        // Add actions:
        Button loadPadFile = new Button("Create Report", new Command() {
            @Override
            public void execute() {
                new CardServiceSimulationReconciliationCreateDialog((Presenter) getPresenter()).show();
            }
        });
        getDataTablePanel().addUpperActionItem(loadPadFile.asWidget());
    }

}
