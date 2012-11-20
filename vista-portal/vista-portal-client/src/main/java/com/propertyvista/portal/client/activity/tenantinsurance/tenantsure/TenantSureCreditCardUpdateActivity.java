/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;

public class TenantSureCreditCardUpdateActivity extends SecurityAwareActivity implements TenantSureCreditCardUpdateView.Presenter {

    private final TenantSureCreditCardUpdateView view;

    public TenantSureCreditCardUpdateActivity() {
        view = PortalViewFactory.instance(TenantSureCreditCardUpdateView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        view.setPresenter(this);

        LeasePaymentMethod pm = EntityFactory.create(LeasePaymentMethod.class);
        pm.type().setValue(PaymentType.CreditCard);
        view.populate(pm);

        panel.setWidget(view);
    }

    @Override
    public void save(LeasePaymentMethod entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

}
