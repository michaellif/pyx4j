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
package com.propertyvista.portal.shared.activity.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;
import com.propertyvista.portal.shared.ui.landing.PasswordResetRequestWizardView;
import com.propertyvista.portal.shared.ui.landing.PasswordResetRequestWizardView.PasswordResetRequestWizardPresenter;

public class PasswordResetRequestWizardActivity extends AbstractWizardActivity<PasswordRetrievalRequest> implements PasswordResetRequestWizardPresenter {

    private static final I18n i18n = I18n.get(PasswordResetRequestWizardActivity.class);

    public PasswordResetRequestWizardActivity(Place place) {
        super(PasswordResetRequestWizardView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        getView().populate(EntityFactory.create(PasswordRetrievalRequest.class));
        createNewCaptchaChallenge();
    }

    @Override
    public void submit() {
        ClientContext.getAuthenticationService().requestPasswordReset(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                PasswordResetRequestWizardActivity.super.submit();
                Notification message = new Notification(i18n.tr("A link to the password reset page was sent to your email."),
                        i18n.tr("Password reset request has been submitted"), NotificationType.INFO);
                PortalSite.getPlaceController().showNotification(message);
            }

            @Override
            public void onFailure(Throwable caught) {
                createNewCaptchaChallenge();
                MessageDialog.error(i18n.tr("Failed to send password reset request"), caught.getLocalizedMessage());
            }
        }, getView().getValue());
    }

    public void createNewCaptchaChallenge() {

        final PasswordResetRequestWizardView view = ((PasswordResetRequestWizardView) getView());

        if (CaptchaComposite.isPublicKeySet()) {
            view.createNewCaptchaChallenge();
            // view.displayPasswordResetFailedMessage();
        } else {
            GWT.<AuthenticationService> create(ResidentAuthenticationService.class).obtainRecaptchaPublicKey(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CaptchaComposite.setPublicKey(result);
                    view.createNewCaptchaChallenge();
                }
            });
        }
    }

}
