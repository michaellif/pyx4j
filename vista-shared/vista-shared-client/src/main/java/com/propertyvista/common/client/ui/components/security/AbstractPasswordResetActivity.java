/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordResetService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetQuestion;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class AbstractPasswordResetActivity extends AbstractActivity implements PasswordResetView.Presenter {

    private static final I18n i18n = I18n.get(AbstractPasswordResetActivity.class);

    protected final PasswordResetView view;

    protected final AbstractPasswordResetService service;

    public AbstractPasswordResetActivity(Place place, PasswordResetView view, AbstractPasswordResetService service) {
        this.view = view;
        this.service = service;
        view.setPresenter(this);
        withPlace(place);
    }

    public AbstractPasswordResetActivity withPlace(Place place) {
        return this;
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
        service.resetPassword(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info(i18n.tr("Your password has been reset successfully!"));
                //let AppPlaceDispatcher manage this. e.g. go to default place as defined in AppPlaceDispatcher. Or to target URL.
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        }, request);

    }

}
