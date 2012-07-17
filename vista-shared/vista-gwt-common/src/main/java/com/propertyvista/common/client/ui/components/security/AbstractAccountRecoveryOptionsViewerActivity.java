/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.portal.rpc.shared.services.AbstractAccountRecoveryOptionsService;

// TODO create a special place class
public class AbstractAccountRecoveryOptionsViewerActivity extends AbstractActivity implements AccountRecoveryOptionsViewerView.Presenter {

    private final int CANCEL_TIMEOUT = 5 * 1000 * 60;

    private final AbstractAccountRecoveryOptionsService service;

    private final AccountRecoveryOptionsViewerView view;

    private boolean isCancelled;

    private Timer cancelationTimer;

    private final CrudAppPlace place;

    public AbstractAccountRecoveryOptionsViewerActivity(CrudAppPlace place, AccountRecoveryOptionsViewerView view, AbstractAccountRecoveryOptionsService service) {
        this.place = place;
        this.service = service;
        this.view = view;

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.reset();
        panel.setWidget(view);
        view.setPresenter(this);

        cancelationTimer = new Timer() {
            @Override
            public void run() {
                AbstractAccountRecoveryOptionsViewerActivity.this.cancel();
            }

        };
        cancelationTimer.schedule(CANCEL_TIMEOUT);

        populate();
    }

    @Override
    public void populate() {
        if (!isCancelled) {
            AuthenticationRequest authRequest = EntityFactory.create(AuthenticationRequest.class);
            authRequest.password().setValue(getCurrentPassword());

            service.obtainRecoveryOptions(new DefaultAsyncCallback<AccountRecoveryOptionsDTO>() {

                @Override
                public void onSuccess(AccountRecoveryOptionsDTO result) {
                    view.populate(result);
                }
            }, authRequest);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public void edit() {
        if (!isCancelled) {
            String password = getCurrentPassword();
            place.formEditorPlace(new Key(-1));
            place.placeArg("password", password);
            AppSite.getPlaceController().goTo(place);
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
        History.back();
    }

    @Override
    public void view(Key entityId) {
        populate();
    }

    @Override
    public void approveFinal() {
        throw new IllegalStateException("ehm");
    }

    @Override
    public void onStop() {
        if (cancelationTimer != null) {
            cancelationTimer.cancel();
            cancelationTimer = null;
        }
        super.onStop();
    }

    private String getCurrentPassword() {
        return place.getFirstArg("password");
    }

}
