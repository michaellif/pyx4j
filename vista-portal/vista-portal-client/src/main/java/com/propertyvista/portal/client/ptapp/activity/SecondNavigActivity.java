/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.SecondNavigView;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.TenantTabInfo;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.place.AppPlaceListing;
import com.pyx4j.site.rpc.AppPlace;

public class SecondNavigActivity extends AbstractActivity implements SecondNavigView.SecondNavigPresenter {

    private final SecondNavigView view;

    private final PlaceController placeController;

    private final AppPlaceListing appPlaceListing;

    @Inject
    public SecondNavigActivity(SecondNavigView view, PlaceController placeController, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.placeController = placeController;
        this.appPlaceListing = appPlaceListing;
        view.setPresenter(this);
    }

    public SecondNavigActivity withPlace(Place place) {

        if (place.getClass() == SiteMap.Info.class || place.getClass() == SiteMap.Financial.class) {

            // fill dumb tenants value:

            ApplicationProgress ap = EntityFactory.create(ApplicationProgress.class);
            TenantTabInfo tti = EntityFactory.create(TenantTabInfo.class);
            tti.fullName().setValue("Vasia I. Pupkin");
            ap.tenants().add(tti);
            tti = EntityFactory.create(TenantTabInfo.class);
            tti.fullName().setValue("Masha Pupkina");
            ap.tenants().add(tti);
            tti = EntityFactory.create(TenantTabInfo.class);
            tti.fullName().setValue("Petya V. Pupkin");
            ap.tenants().add(tti);

            PtAppWizardManager.instance().getCurrentApplication().progress.tenants().setValue(ap.tenants().getValue());

            view.show();
        } else
            view.hide();

        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        placeController.goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return appPlaceListing.getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<TenantTabInfo> getTenantTabsInfo() {
        return PtAppWizardManager.instance().getCurrentApplication().progress.tenants();
    }

    @Override
    public Place getWhere() {
        return placeController.getWhere();
    }

}
