/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class AbstractPasswordResetRequestActivity extends AbstractActivity implements PasswordResetRequestView.PasswordResetRequestPresenter {

    private final static I18n i18n = I18n.get(AbstractPasswordResetRequestActivity.class);

    private final PasswordResetRequestView view;

    private final AppPlace loginPlace;

    /**
     * 
     * @param place
     * @param view
     * @param loginPlace
     *            a place to return to after a successful request.
     */
    public AbstractPasswordResetRequestActivity(Place place, PasswordResetRequestView view, AppPlace loginPlace) {
        this.loginPlace = loginPlace;
        this.view = view;
        this.view.setPresenter(this);
        withPlace(place);
    }

    public AbstractPasswordResetRequestActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void requestPasswordReset(PasswordRetrievalRequest request) {
        AsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                onPasswordResetRequestSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                createNewCaptchaChallenge();
                view.displayPasswordResetFailedMessage();
            }
        };
        ClientContext.getAuthenticationService().requestPasswordReset(callback, request);
    }

    /**
     * Default implementation of successful password request: <br/>
     * 
     * <li>Show dialog with happy message (something like "an email with password reset instructions has been sent, check your mailbox").</li>
     * 
     * <li>Go to login page (defined by the place that was passed via the constructor).</li>
     */
    protected void onPasswordResetRequestSuccess() {
        MessageDialog.info(i18n.tr("A link to the password reset page was sent to your email"));
        AppSite.getPlaceController().goTo(loginPlace);
    }

    @Override
    public void createNewCaptchaChallenge() {
        if (CaptchaComposite.isPublicKeySet()) {
            view.createNewCaptchaChallenge();
            view.displayPasswordResetFailedMessage();
        } else {
            ClientContext.getAuthenticationService().obtainRecaptchaPublicKey(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CaptchaComposite.setPublicKey(result);
                    view.createNewCaptchaChallenge();
                }
            });
        }
    }
}
