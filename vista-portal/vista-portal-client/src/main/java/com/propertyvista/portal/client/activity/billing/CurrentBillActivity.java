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
package com.propertyvista.portal.client.activity.billing;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.billing.CurrentBillView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class CurrentBillActivity extends SecurityAwareActivity implements CurrentBillView.Presenter {
    private final CurrentBillView view;

    public CurrentBillActivity(Place place) {
        this.view = PortalViewFactory.instance(CurrentBillView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        //TODO implement a service call
        view.populate(EntityFactory.create(BillDTO.class));
    }

    @Override
    public void changePaymentMethod() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());
    }

    @Override
    public void changeAuthorization(boolean authorized) {
        // TODO Implement

    }

    @Override
    public void payBill() {
        // TODO Implement

    }
}
