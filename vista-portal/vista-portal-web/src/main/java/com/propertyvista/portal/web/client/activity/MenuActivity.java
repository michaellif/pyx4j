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

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.MenuView;

public class MenuActivity extends AbstractActivity implements MenuView.MenuPresenter {

    private final MenuView view;

    public MenuActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(MenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        updateView();
        eventBus.addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                updateView();
            }
        });

        eventBus.addHandler(ContextChangeEvent.getType(), new ContextChangeHandler() {

            @Override
            public void onContextChange(ContextChangeEvent event) {
                updateView();
            }
        });

        AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.resizeComponents));
    }

    private void updateView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
        } else {
            view.onLogedOut();
        }
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
