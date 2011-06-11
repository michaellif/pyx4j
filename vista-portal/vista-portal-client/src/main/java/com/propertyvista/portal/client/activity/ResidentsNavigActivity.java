/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.ResidentsNavigView;
import com.propertyvista.portal.client.ui.ResidentsNavigView.ResidentsNavigPresenter;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.MainNavigDTO;

public class ResidentsNavigActivity extends AbstractActivity implements ResidentsNavigPresenter {

    private final ResidentsNavigView view;

    public ResidentsNavigActivity(Place place) {
        this.view = (ResidentsNavigView) PortalViewFactory.instance(ResidentsNavigView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    @Override
    public String getCaption(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getCaption();
    }

    @Override
    public MainNavigDTO getResidentsNavig() {
        MainNavigDTO navig = EntityFactory.create(MainNavigDTO.class);
//TODO
//        PageDescriptor navitem1 = EntityFactory.create(PageDescriptor.class);
//        navitem1.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new Residents.Navigator.TenantProfile()));
//        navitem1.caption().setValue("Tenant Profile");
//        navig.items().add(navitem1);
//
//        PageDescriptor navitem2 = EntityFactory.create(PageDescriptor.class);
//        navitem2.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new Residents.Navigator.Maintenance()));
//        navitem2.title().setValue("Maintenance");
//        navig.items().add(navitem2);
//
//        PageDescriptor navitem3 = EntityFactory.create(PageDescriptor.class);
//        navitem3.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new Residents.Navigator.Payment()));
//        navitem3.title().setValue("Payment");
//        navig.items().add(navitem3);
//
//        PageDescriptor navitem4 = EntityFactory.create(PageDescriptor.class);
//        navitem4.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new Residents.Navigator.LeaseApplication()));
//        navitem4.title().setValue("Lease Application");
//        navig.items().add(navitem4);

        return navig;
    }

    public ResidentsNavigActivity withPlace(Place place) {
        return this;
    }
}
