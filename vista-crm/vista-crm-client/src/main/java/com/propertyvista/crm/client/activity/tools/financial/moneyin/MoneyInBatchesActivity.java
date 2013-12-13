/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.financial.moneyin;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInBatchesView;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchesActivity extends AbstractActivity implements MoneyInBatchesView.Presenter {

    private final MoneyInBatchesView view;

    private final ListDataProvider<MoneyInBatchDTO> listDataProvider;

    public MoneyInBatchesActivity() {
        view = CrmSite.getViewFactory().getView(MoneyInBatchesView.class);
        listDataProvider = new ListDataProvider<MoneyInBatchDTO>();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        listDataProvider.addDataDisplay(view.batches());
        panel.setWidget(view);
    }

    @Override
    public Object getKey(MoneyInBatchDTO item) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void search() {
        // TODO Auto-generated method stub

    }

    @Override
    public void openBatch(MoneyInBatchDTO batch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(MoneyInBatchDTO batch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sort(String memberPath, boolean isAscending) {
        // TODO Auto-generated method stub

    }

    // not used  // TODO ?
    //@formatter:off
    @Override public AppPlace getPlace() { return null; }    
    @Override public void populate() {}
    @Override public void refresh() {}
    //@formatter:on

}
