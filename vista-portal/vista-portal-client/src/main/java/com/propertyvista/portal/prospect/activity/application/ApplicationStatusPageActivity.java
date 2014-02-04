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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;
import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.ui.application.ApplicationStatusPageView;
import com.propertyvista.portal.prospect.ui.application.ApplicationStatusPageView.ApplicationStatusPagePresenter;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class ApplicationStatusPageActivity extends SecurityAwareActivity implements ApplicationStatusPagePresenter {

    private final ApplicationStatusPageView view;

    private MasterOnlineApplicationStatus masterAppStatus;

    public ApplicationStatusPageActivity(AppPlace place) {
        this.view = ProspectPortalSite.getViewFactory().getView(ApplicationStatusPageView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        GWT.<ApplicationStatusService> create(ApplicationStatusService.class).retrieveMasterApplicationStatus(
                new DefaultAsyncCallback<MasterOnlineApplicationStatus>() {
                    @Override
                    public void onSuccess(MasterOnlineApplicationStatus result) {
                        masterAppStatus = result;

                        if (getUserApplicationStatus().status().getValue() == OnlineApplication.Status.Invited) {
                            AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application());
                        } else {
                            panel.setWidget(view);
                            view.populate(result);
                        }
                    }
                });
    }

    @Override
    public void onCancel() {
        view.reset();
    }

    @Override
    public void onStop() {
        onCancel();
    }

    @Override
    public OnlineApplicationStatus getUserApplicationStatus() {

        for (OnlineApplicationStatus appStatus : masterAppStatus.individualApplications()) {
            if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(appStatus.customer().user().getPrimaryKey())) {
                return appStatus;
            }
        }
        throw new Error("Application is not found");
    }
}
