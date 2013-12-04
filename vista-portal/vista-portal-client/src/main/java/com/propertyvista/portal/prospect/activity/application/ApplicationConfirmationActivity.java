/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.application;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.ui.application.ApplicationConfirmationView;
import com.propertyvista.portal.prospect.ui.application.ApplicationConfirmationView.ApplicationConfirmationPresenter;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class ApplicationConfirmationActivity extends SecurityAwareActivity implements ApplicationConfirmationPresenter {

    private final ApplicationConfirmationView view;

    public ApplicationConfirmationActivity(AppPlace place) {
        this.view = ProspectPortalSite.getViewFactory().getView(ApplicationConfirmationView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }

}
