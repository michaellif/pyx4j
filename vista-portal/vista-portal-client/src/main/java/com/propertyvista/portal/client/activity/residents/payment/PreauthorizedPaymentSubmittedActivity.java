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

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents.Financial;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentSubmittedActivity extends SecurityAwareActivity implements PreauthorizedPaymentSubmittedView.Presenter {

    private final PreauthorizedPaymentSubmittedView view;

    private final PreauthorizedPaymentDTO value;

    public PreauthorizedPaymentSubmittedActivity(Place place) {
        this.view = ResidentsViewFactory.instance(PreauthorizedPaymentSubmittedView.class);
        this.view.setPresenter(this);

        assert (place instanceof Financial.AutoPay.PreauthorizedPaymentSubmitted);
        value = ((Financial.AutoPay.PreauthorizedPaymentSubmitted) place).getPreauthorizedPayment();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        if (value != null && !value.isEmpty()) {
            view.populate(value);
        }
    }

    @Override
    public void save(PreauthorizedPaymentDTO entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }
}
