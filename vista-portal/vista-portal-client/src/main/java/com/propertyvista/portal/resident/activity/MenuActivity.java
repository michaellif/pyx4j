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

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.MenuView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class MenuActivity extends AbstractActivity implements MenuView.MenuPresenter {

    private final MenuView view;

    private final Place place;

    public MenuActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(MenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setUserName(ClientContext.getUserVisit().getName());
        view.setLeasesSelectorEnabled(SecurityController.checkAnyBehavior(PortalResidentBehavior.HasMultipleLeases));
        view.setMenuVisible(!(place instanceof ResidentPortalSiteMap.MoveIn.MoveInWizard)
                && !(place instanceof ResidentPortalSiteMap.MoveIn.NewTenantWelcomePage)
                && !(place instanceof ResidentPortalSiteMap.MoveIn.NewGuarantorWelcomePage) && !(place instanceof ResidentPortalSiteMap.LeaseContextSelection));
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
    }

}
