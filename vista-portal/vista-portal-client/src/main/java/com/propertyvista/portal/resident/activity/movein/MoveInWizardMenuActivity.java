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
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager.MoveInWizardState;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeEvent;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeHandler;
import com.propertyvista.portal.resident.ui.movein.MoveInWizardMenuView;
import com.propertyvista.portal.resident.ui.movein.MoveInWizardMenuView.MoveInWizardMenuPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class MoveInWizardMenuActivity extends AbstractActivity implements MoveInWizardMenuPresenter {

    private final MoveInWizardMenuView view;

    private final Place place;

    public MoveInWizardMenuActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(MoveInWizardMenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        view.setUserName(ClientContext.getUserVisit().getName());
        panel.setWidget(view);

        eventBus.addHandler(MoveInWizardStateChangeEvent.getType(), new MoveInWizardStateChangeHandler() {

            @Override
            public void onStateChange(MoveInWizardStateChangeEvent event) {
                view.setMenuVisible(MoveInWizardManager.getMoveInWizardState() == MoveInWizardState.wizard
                        || (MoveInWizardManager.getMoveInWizardState() == MoveInWizardState.confirmation && !(place instanceof ResidentPortalSiteMap.MoveIn.MoveInWizard)));
                view.updateState();
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });

    }
}
