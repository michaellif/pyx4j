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
package com.propertyvista.portal.client.activity.billing;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.dto.BillDTO.Type;
import com.propertyvista.portal.domain.dto.BillListDTO;
import com.propertyvista.portal.domain.dto.BillListDTO.SearchType;

public class BillingHistoryActivity extends SecurityAwareActivity implements BillingHistoryView.Presenter {
    private final BillingHistoryView view;

    public BillingHistoryActivity(Place place) {
        this.view = PortalViewFactory.instance(BillingHistoryView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        //TODO implement a service call
        BillListDTO billHistory = EntityFactory.create(BillListDTO.class);

        if (!VistaTODO.removedForProduction) {
            BillDTO bill = EntityFactory.create(BillDTO.class);
            bill.type().setValue(Type.Bill);
            bill.fromDate().setValue(new LogicalDate(new Date()));
            bill.referenceNo().setValue("645436654");
            bill.amount().setValue(new BigDecimal(1490));
            billHistory.bills().add(bill);

            bill = EntityFactory.create(BillDTO.class);
            bill.type().setValue(Type.Bill);
            bill.fromDate().setValue(new LogicalDate(new Date()));
            bill.referenceNo().setValue("10096654");
            bill.amount().setValue(new BigDecimal(1010));
            billHistory.bills().add(bill);

            bill = EntityFactory.create(BillDTO.class);
            bill.type().setValue(Type.Bill);
            bill.fromDate().setValue(new LogicalDate(new Date()));
            bill.referenceNo().setValue("90056789");
            bill.amount().setValue(new BigDecimal(890));
            billHistory.bills().add(bill);
        }

        view.populate(billHistory);

        panel.setWidget(view);

    }

    @Override
    public void search(SearchType searchType) {
        // TODO Implement

    }

    @Override
    public void view(BillDTO item) {
        // TODO Auto-generated method stub

    }
}
