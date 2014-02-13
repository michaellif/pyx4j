/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.movein.NewGuarantorWelcomePageView;
import com.propertyvista.portal.resident.ui.movein.NewGuarantorWelcomePageView.NewGuarantorWelcomePagePresenter;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class NewGuarantorWelcomePageActivity extends SecurityAwareActivity implements NewGuarantorWelcomePagePresenter {

    private final NewGuarantorWelcomePageView view;

    public NewGuarantorWelcomePageActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().getView(NewGuarantorWelcomePageView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
    }

}
