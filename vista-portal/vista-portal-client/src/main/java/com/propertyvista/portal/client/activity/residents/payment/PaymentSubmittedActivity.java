/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.payment;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.payment.PaymentSubmittedView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents.Financial;

public class PaymentSubmittedActivity extends SecurityAwareActivity implements PaymentSubmittedView.Presenter {

    private final PaymentSubmittedView view;

    private final PaymentRecordDTO paymentRecord;

    public PaymentSubmittedActivity(Place place) {
        this.view = ResidentsViewFactory.instance(PaymentSubmittedView.class);
        this.view.setPresenter(this);
        assert (place instanceof Financial.PaymentSubmitted);
        paymentRecord = ((Financial.PaymentSubmitted) place).getPaymentRecord();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        view.populate(paymentRecord);
    }

    @Override
    public void save(PaymentRecordDTO entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

    @Override
    public void goToAutoPay() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Financial.AutoPay());
    }
}
