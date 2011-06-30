/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.client.ui.residents.CurrentBillView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.ChargeLine.ChargeType;

public class CurrentBillActivity extends SecurityAwareActivity implements CurrentBillView.Presenter {
    private final CurrentBillView view;

    public CurrentBillActivity(Place place) {
        this.view = (CurrentBillView) PortalViewFactory.instance(CurrentBillView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    public CurrentBillActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        //TODO implement a service call
        BillDTO bill = EntityFactory.create(BillDTO.class);

        ChargeLine cLine = EntityFactory.create(ChargeLine.class);
        cLine.type().setValue(ChargeType.monthlyRent);
        cLine.charge().amount().setValue(1200d);
        bill.charges().add(cLine);

        ChargeLine cLine2 = EntityFactory.create(ChargeLine.class);
        cLine2.type().setValue(ChargeType.parking);
        cLine2.charge().amount().setValue(100d);
        bill.charges().add(cLine2);

        ChargeLine cLine3 = EntityFactory.create(ChargeLine.class);
        cLine3.type().setValue(ChargeType.locker);
        cLine3.charge().amount().setValue(100d);
        bill.charges().add(cLine3);

        ChargeLine cLine4 = EntityFactory.create(ChargeLine.class);
        cLine4.type().setValue(ChargeType.cableTV);
        cLine4.charge().amount().setValue(90d);
        bill.charges().add(cLine4);

        bill.dueDate().setValue(new LogicalDate(new Date()));
        view.populate(bill);

        panel.setWidget(view);

    }
}
