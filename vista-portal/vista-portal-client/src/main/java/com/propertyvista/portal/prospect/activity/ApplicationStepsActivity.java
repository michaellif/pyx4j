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
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.prospect.ui.ApplicationStepsView;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicationStepDescriptorsDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;
import com.propertyvista.portal.shared.PortalSite;

public class ApplicationStepsActivity extends AbstractActivity implements ApplicationStepsView.ApplicationStepsPresenter {

    private final ApplicationStepsView view;

    private final Place place;

    public ApplicationStepsActivity(Place place) {
        this.place = place;
        this.view = PortalSite.getViewFactory().getView(ApplicationStepsView.class);
        assert (view != null);
        view.setPresenter(this);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        GWT.<ApplicationStatusService> create(ApplicationStatusService.class).retrieveApplicationStepDescriptors(
                new DefaultAsyncCallback<ApplicationStepDescriptorsDTO>() {
                    @Override
                    public void onSuccess(ApplicationStepDescriptorsDTO result) {
                        view.setStepButtons(result);
                        panel.setWidget(view);
                    }
                });
    }

}
