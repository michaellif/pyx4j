/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.client.ui.residents.BillingHistoryView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.BillDTO.BillType;
import com.propertyvista.portal.domain.dto.BillListDTO;
import com.propertyvista.portal.domain.dto.BillListDTO.SearchType;
import com.propertyvista.portal.domain.util.DomainUtil;

public class BillingHistoryActivity extends SecurityAwareActivity implements BillingHistoryView.Presenter {
    private final BillingHistoryView view;

    public BillingHistoryActivity(Place place) {
        this.view = (BillingHistoryView) PortalViewFactory.instance(BillingHistoryView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        //TODO implement a service call
        BillListDTO billHistory = EntityFactory.create(BillListDTO.class);

        BillDTO bill = EntityFactory.create(BillDTO.class);
        bill.type().setValue(BillType.Bill);
        bill.paidOn().setValue(new LogicalDate(new Date()));
        bill.transactionID().setValue("645436654");
        bill.total().set(DomainUtil.createMoney(1490d));
        billHistory.bills().add(bill);

        bill = EntityFactory.create(BillDTO.class);
        bill.type().setValue(BillType.Bill);
        bill.paidOn().setValue(new LogicalDate(new Date()));
        bill.transactionID().setValue("10096654");
        bill.total().set(DomainUtil.createMoney(1010d));
        billHistory.bills().add(bill);

        bill = EntityFactory.create(BillDTO.class);
        bill.type().setValue(BillType.Bill);
        bill.paidOn().setValue(new LogicalDate(new Date()));
        bill.transactionID().setValue("90056789");
        bill.total().amount().setValue(1350d);
        bill.total().set(DomainUtil.createMoney(890d));
        billHistory.bills().add(bill);

        view.populate(billHistory);

        panel.setWidget(view);

    }

    @Override
    public void search(SearchType searchType) {
        // TODO Implement

    }

}
