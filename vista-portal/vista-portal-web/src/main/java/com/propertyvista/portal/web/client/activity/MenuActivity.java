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
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.web.client.ui.MenuView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class MenuActivity extends AbstractActivity implements MenuView.MenuPresenter {

    private final MenuView view;

    public MenuActivity(Place place) {
        this.view = PortalWebViewFactory.instance(MenuView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public MenuActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.resizeComponents));
    }

    @Override
    public void navigTo(AppPlace place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public AppPlace getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

}
