/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.l1generation;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchView;
import com.propertyvista.crm.client.ui.tools.l1generation.L1DelinquentLeaseSearchViewImpl;
import com.propertyvista.crm.rpc.dto.legal.common.LegalActionCandidateDTO;

public class L1DelinquentLeaseSearchActivity extends AbstractActivity implements L1DelinquentLeaseSearchView.Presenter {

    private final L1DelinquentLeaseSearchView view;

    private final ListDataProvider<LegalActionCandidateDTO> dataProvider;

    private final MultiSelectionModel<LegalActionCandidateDTO> selectionModel;

    public L1DelinquentLeaseSearchActivity() {
        view = CrmSite.getViewFactory().getView(L1DelinquentLeaseSearchView.class);
        dataProvider = new ListDataProvider<LegalActionCandidateDTO>(makeMockCandidates());
        selectionModel = new MultiSelectionModel<LegalActionCandidateDTO>(L1DelinquentLeaseSearchViewImpl.LeaseIdProvider.INSTANCE);
        selectionModel.setSelected(dataProvider.getList().get(0), true);
        selectionModel.setSelected(dataProvider.getList().get(1), true);
        selectionModel.setSelected(dataProvider.getList().get(2), true);
        selectionModel.setSelected(dataProvider.getList().get(8), true);
        selectionModel.setSelected(dataProvider.getList().get(9), true);
        selectionModel.setSelected(dataProvider.getList().get(10), true);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    @Override
    public AbstractDataProvider<LegalActionCandidateDTO> getDataProvider() {
        return dataProvider;
    }

    @Override
    public SelectionModel<? super LegalActionCandidateDTO> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void toggleSelectAll(Boolean selected) {
        for (LegalActionCandidateDTO c : dataProvider.getList()) {
            selectionModel.setSelected(c, selected);
        }

    }

    private List<LegalActionCandidateDTO> makeMockCandidates() {
        List<LegalActionCandidateDTO> mockCandidates = new LinkedList<LegalActionCandidateDTO>();
        for (int i = 1; i < 101; ++i) {
            mockCandidates.add(makeMockCandidate(i));
        }
        return mockCandidates;
    }

    private LegalActionCandidateDTO makeMockCandidate(int n) {
        LegalActionCandidateDTO c = EntityFactory.create(LegalActionCandidateDTO.class);
        c.leaseIdStub().setPrimaryKey(new Key(n));
        c.propertyCode().setValue(n % 5 != 0 ? "B1" : "B2");
        c.leaseId().setValue("t00000" + n);
        c.unit().setValue("" + (100 + n));
        c.streetAddress().setValue("1234 The West Mall, Toronto ON A1A 1A1");
        c.owedAmount().setValue(new BigDecimal("800").add(new BigDecimal(100 + Random.nextInt(500))));
        return c;
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }

    @Override
    public AppPlace getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

}
