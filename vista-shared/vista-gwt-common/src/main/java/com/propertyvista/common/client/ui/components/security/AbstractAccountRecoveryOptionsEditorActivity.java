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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.portal.rpc.shared.services.AbstractAccountRecoveryOptionsService;

public class AbstractAccountRecoveryOptionsEditorActivity extends AbstractActivity implements AccountRecoveryOptionsEditorView.Presenter {

    private static final I18n i18n = I18n.get(AbstractAccountRecoveryOptionsEditorActivity.class);

    private final int CANCEL_TIMEOUT = 5 * 60 * 1000;

    private final CrudAppPlace place;

    private final AccountRecoveryOptionsEditorView view;

    private final AbstractAccountRecoveryOptionsService service;

    private boolean isCancelled;

    private Timer cancelationTimer;

    public AbstractAccountRecoveryOptionsEditorActivity(CrudAppPlace place, AccountRecoveryOptionsEditorView view, AbstractAccountRecoveryOptionsService service) {
        this.place = place;
        this.view = view;
        this.service = service;
    }

    /** Warning: this method was made <code>final</code> because it works asynchronously */
    @Override
    public final void populate() {
        if (!isCancelled) {
            AuthenticationRequest authRequest = EntityFactory.create(AuthenticationRequest.class);
            authRequest.password().setValue(getCurrentPassword());

            service.obtainRecoveryOptions(new DefaultAsyncCallback<AccountRecoveryOptionsDTO>() {
                @Override
                public void onSuccess(AccountRecoveryOptionsDTO result) {
                    view.setSecurityQuestionRequired(SecurityController.checkBehavior(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion));
                    view.populate(result);
                }
            }, authRequest);
        }

    }

    @Override
    public void refresh() {

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.reset();
        panel.setWidget(view);
        view.setPresenter(this);

        cancelationTimer = new Timer() {
            @Override
            public void run() {
                AbstractAccountRecoveryOptionsEditorActivity.this.cancel();
            }

        };
        cancelationTimer.schedule(CANCEL_TIMEOUT);

        populate();
    }

    @Override
    public void save() {
        if (!isCancelled) {
            if (!isCancelled) {
                service.updateRecoveryOptions(new AsyncCallback<VoidSerializable>() {
                    @Override
                    public void onSuccess(VoidSerializable result) {
                        view.reset();
                        MessageDialog.info(i18n.tr("Account recovery options were updated successfully"));
                        History.back();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new Error(caught);
                    }
                }, view.getValue());
            }
        }
    }

    @Override
    public void apply() {

    }

    @Override
    public void cancel() {
        isCancelled = true;
        History.back();
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
