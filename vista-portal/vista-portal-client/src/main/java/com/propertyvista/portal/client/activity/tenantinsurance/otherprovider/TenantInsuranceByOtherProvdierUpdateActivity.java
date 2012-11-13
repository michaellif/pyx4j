/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.otherprovider;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceByOtherProviderDetailsDTO;

public class TenantInsuranceByOtherProvdierUpdateActivity extends SecurityAwareActivity implements TenantInsuranceByOtherProviderUpdateView.Presenter {

    private final TenantInsuranceByOtherProviderUpdateView view;

    public TenantInsuranceByOtherProvdierUpdateActivity(Place place) {
        view = PortalViewFactory.instance(TenantInsuranceByOtherProviderUpdateView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        view.setPresenter(this);
        view.populate(EntityFactory.create(TenantInsuranceByOtherProviderDetailsDTO.class));
        panel.setWidget(view);

    }

    @Override
    public void save(TenantInsuranceByOtherProviderDetailsDTO entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancel() {
        History.back();
    }

}
