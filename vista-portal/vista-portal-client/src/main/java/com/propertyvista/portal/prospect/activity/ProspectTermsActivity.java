/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.PortalVistaTermsService;
import com.propertyvista.portal.shared.ui.TermsView;

public class ProspectTermsActivity extends AbstractActivity {

    private final TermsView view;

    private final Place place;

    public ProspectTermsActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().instantiate(TermsView.class);
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        if (place instanceof ProspectPortalSiteMap.TermsAndConditions) {
            GWT.<PortalVistaTermsService> create(PortalVistaTermsService.class).getResidentPortalTerms(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        }

        panel.setWidget(view);
    }
}
