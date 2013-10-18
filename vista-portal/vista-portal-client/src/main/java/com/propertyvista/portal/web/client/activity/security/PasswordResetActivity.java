/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AbstractPasswordResetService;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetQuestion;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;

import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.portal.web.client.PortalWebSite;

public class PasswordResetActivity extends AbstractActivity implements PasswordResetView.Presenter {

    private static final I18n i18n = I18n.get(AbstractPasswordResetActivity.class);

    protected final PasswordResetView view;

    protected final AbstractPasswordResetService service;

    public PasswordResetActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(PasswordResetView.class);
        this.service = GWT.<AbstractPasswordResetService> create(PortalPasswordResetService.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        service.obtainPasswordResetQuestion(new DefaultAsyncCallback<PasswordResetQuestion>() {

            @Override
            public void onSuccess(PasswordResetQuestion result) {
                view.setQuestion(result);
            }
        });
    }

    @Override
    public void resetPassword(PasswordChangeRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                MessageDialog_v2.info(i18n.tr("Your password has been reset successfully!"));
                //let AppPlaceDispatcher manage this. e.g. go to default place as defined in AppPlaceDispatcher. Or to target URL.
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        };

        service.resetPassword(callback, request);

    }

}
