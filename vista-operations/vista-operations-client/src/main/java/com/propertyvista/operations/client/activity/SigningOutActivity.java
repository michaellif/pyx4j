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
package com.propertyvista.operations.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.operations.rpc.OperationsSiteMap;

public class SigningOutActivity extends AbstractActivity {

    public SigningOutActivity(Place place) {
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                logoutDeferred();
            }
        });
    }

    private void logoutDeferred() {
        ClientContext.logout(new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Login());
            }
        });
    }
}
