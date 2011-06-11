/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.MainNavigView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.site.PageDescriptor;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.MainNavigPresenter {

    private final MainNavigView view;

    public MainNavigActivity(Place place) {
        this.view = (MainNavigView) PortalViewFactory.instance(MainNavigView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public MainNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        PortalSite.getPortalSiteServices().retrieveMainNavig(new DefaultAsyncCallback<PageDescriptor>() {
            @Override
            public void onSuccess(PageDescriptor navig) {
                List<NavigItem> items = new ArrayList<NavigItem>();
                for (PageDescriptor descriptor : navig.childPages()) {
                    if (PageDescriptor.Type.staticContent.equals(descriptor.type().getValue())) {
                        items.add(new NavigItem(descriptor.caption().getStringView(), descriptor.caption().getStringView()));
                    } else {
                        AppPlace place = NavigItem.convertTypeToPlace(descriptor.type().getValue());
                        items.add(new NavigItem(place, AppSite.getHistoryMapper().getPlaceInfo(place).getCaption()));
                    }
                }
                view.setMainNavig(items);
            }
        });
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

}
