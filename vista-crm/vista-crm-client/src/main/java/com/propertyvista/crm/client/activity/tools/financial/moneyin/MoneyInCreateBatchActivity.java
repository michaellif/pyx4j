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
package com.propertyvista.crm.client.activity.tools.financial.moneyin;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInCreateBatchView;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;

public class MoneyInCreateBatchActivity extends AbstractActivity implements MoneyInCreateBatchView.Presenter {

    private final MoneyInCreateBatchView view;

    private final ListDataProvider<MoneyInCandidateDTO> dataProvider;

    private final MultiSelectionModel<MoneyInCandidateDTO> selectionModel;

    public MoneyInCreateBatchActivity() {
        view = CrmSite.getViewFactory().getView(MoneyInCreateBatchView.class);
        dataProvider = new ListDataProvider<MoneyInCandidateDTO>(makeMockCandidates(), new ProvidesKey<MoneyInCandidateDTO>() {
            @Override
            public Object getKey(MoneyInCandidateDTO item) {
                return item.leaseIdStub().getPrimaryKey();
            }
        });
        selectionModel = new MultiSelectionModel<MoneyInCandidateDTO>(dataProvider.getKeyProvider());
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    @Override
    public AbstractDataProvider<MoneyInCandidateDTO> getDataProvider() {
        return this.dataProvider;
    }

    @Override
    public SelectionModel<MoneyInCandidateDTO> getSelectionModel() {
        return this.selectionModel;
    }

    @Override
    public void search() {
        // TODO Auto-generated method stub        
    }

    @Override
    public void setProcessCandidate(MoneyInCandidateDTO candidate, boolean process) {
        // TODO Auto-generated method stub        
    }

    @Override
    public void setAmount(MoneyInCandidateDTO candidate, BigDecimal amountToPay) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCheckNumber(MoneyInCandidateDTO candidate, BigDecimal chequeNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createBatch() {
        // TODO Auto-generated method stub

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

    private List<MoneyInCandidateDTO> makeMockCandidates() {
        List<MoneyInCandidateDTO> mockCandidates = new LinkedList<MoneyInCandidateDTO>();
        for (int i = 1; i < 101; ++i) {
            mockCandidates.add(makeMockCandidate(i));
        }
        return mockCandidates;
    }

    private MoneyInCandidateDTO makeMockCandidate(int n) {
        MoneyInCandidateDTO c = EntityFactory.create(MoneyInCandidateDTO.class);
        c.leaseIdStub().setPrimaryKey(new Key(n));
        c.building().setValue(n % 5 != 0 ? "B1" : "B2");
        c.unit().setValue("" + (100 + n));
        c.leaseId().setValue("t00000" + n);

        c.prepayments().setValue(new BigDecimal("0.00"));
        c.totalOutstanding().setValue(new BigDecimal("1077.00"));

        return c;
    }

}
