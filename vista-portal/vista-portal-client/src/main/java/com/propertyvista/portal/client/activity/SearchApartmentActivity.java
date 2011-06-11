/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.searchapt.SearchApartmentView;
import com.propertyvista.portal.client.ui.viewfactories.PropertySearchViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class SearchApartmentActivity extends AbstractActivity implements SearchApartmentView.Presenter {

    private final SearchApartmentView view;

    public SearchApartmentActivity(Place place) {
        this.view = (SearchApartmentView) PropertySearchViewFactory.instance(SearchApartmentView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void search() {
        PropertySearchCriteria criteria = view.getValue();
        Map<String, String> args = EntityArgsConverter.convertToArgs(criteria);
        AppPlace place = new PortalSiteMap.FindApartment.PropertyMap();
        place.putAllArgs(args);
        AppSite.getPlaceController().goTo(place);
    }

    public SearchApartmentActivity withPlace(Place place) {
        return this;
    }

}
