/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.application;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.ui.application.ApplicationStatusPageView;
import com.propertyvista.portal.prospect.ui.application.ApplicationStatusPageView.ApplicationStatusPagePresenter;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class ApplicationStatusPageActivity extends SecurityAwareActivity implements ApplicationStatusPagePresenter {

    private final ApplicationStatusPageView view;

    public ApplicationStatusPageActivity(AppPlace place) {
        this.view = ProspectPortalSite.getViewFactory().getView(ApplicationStatusPageView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

//        GWT.<BillingService> create(BillingService.class).retreiveBillingHistory(new DefaultAsyncCallback<BillingHistoryDTO>() {
//            @Override
//            public void onSuccess(BillingHistoryDTO result) {
//                 view.populate(result);
//            }
//        });
    }

}
