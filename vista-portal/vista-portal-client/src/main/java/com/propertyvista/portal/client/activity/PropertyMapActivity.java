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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.PropertyMapView;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class PropertyMapActivity extends AbstractActivity implements PropertyMapView.Presenter {

    private final PropertyMapView view;

    private PropertySearchCriteria criteria;

    @Inject
    public PropertyMapActivity(PropertyMapView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public PropertyMapActivity withPlace(Place place) {
        Map<String, String> args = ((AppPlace) place).getArgs();
        criteria = EntityArgsConverter.createFromArgs(PropertySearchCriteria.class, args);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

        PortalSite.getPortalSiteServices().retrievePropertyList(new DefaultAsyncCallback<PropertyListDTO>() {
            @Override
            public void onSuccess(PropertyListDTO properties) {
                view.populate(criteria, properties);
                //System.out.println(properties);
            }
        }, criteria);

    }

    @Override
    public void showPropertyDetails(PropertyDTO property) {
        AppPlace place = new PortalSiteMap.FindApartment.ApartmentDetails();
        HashMap<String, String> args = new HashMap<String, String>();
        args.put(PortalSiteMap.ARG_PROPERTY_ID, property.id().getValue().toString());
        place.setArgs(args);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void refineSearch() {
        PropertySearchCriteria criteria = view.getValue();
        Map<String, String> args = EntityArgsConverter.convertToArgs(criteria);
        AppPlace place = new PortalSiteMap.FindApartment.PropertyMap();
        place.setArgs(args);
        AppSite.getPlaceController().goTo(place);
    }

}
