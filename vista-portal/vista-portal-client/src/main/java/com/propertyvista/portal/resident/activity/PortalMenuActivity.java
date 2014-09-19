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
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.PortalMenuView;
import com.propertyvista.portal.resident.ui.PortalMenuView.PortalMenuPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class PortalMenuActivity extends AbstractActivity implements PortalMenuPresenter {

    private final PortalMenuView view;

    private final Place place;

    public PortalMenuActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(PortalMenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setUserName(ClientContext.getUserVisit().getName());
        view.setLeasesSelectorEnabled(SecurityController.check(PortalResidentBehavior.HasMultipleLeases));
        view.setMenuVisible(!(place instanceof ResidentPortalSiteMap.LeaseContextSelection));
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
    }

}
