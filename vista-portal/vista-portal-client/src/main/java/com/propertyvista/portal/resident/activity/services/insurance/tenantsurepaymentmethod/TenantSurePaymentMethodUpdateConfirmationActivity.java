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
package com.propertyvista.portal.resident.activity.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.activity.SecurityAwareActivity;
import com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodUpdateConfirmationView;

public class TenantSurePaymentMethodUpdateConfirmationActivity extends SecurityAwareActivity implements
        TenantSurePaymentMethodUpdateConfirmationView.PaymentMethodConfirmationPresenter {

    private final TenantSurePaymentMethodUpdateConfirmationView view;

    public TenantSurePaymentMethodUpdateConfirmationActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().instantiate(TenantSurePaymentMethodUpdateConfirmationView.class);
        this.view.setPresenter(this);

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
    }

}
