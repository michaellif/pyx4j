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
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity.security;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.rpc.portal.shared.services.PasswordChangeUserService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;
import com.propertyvista.portal.shared.ui.security.PasswordChangeWizardView;
import com.propertyvista.portal.shared.ui.security.PasswordChangeWizardView.PasswordChangePresenter;

public class PasswordChangeActivity extends AbstractWizardActivity<PasswordChangeRequest, PasswordChangeWizardView> implements PasswordChangePresenter {

    private static final I18n i18n = I18n.get(PasswordChangeActivity.class);

    public PasswordChangeActivity() {
        super(PasswordChangeWizardView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        getView().populate(EntityFactory.create(PasswordChangeRequest.class));
    }

    @Override
    protected void onFinish() {
        Notification notification = new Notification(null, i18n.tr("Your password was changed successfully!"), NotificationType.INFO);
        PortalSite.getPlaceController().showNotification(notification);
    }

    @Override
    public void finish() {
        GWT.<PasswordChangeUserService> create(PasswordChangeUserService.class).changePassword(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                PasswordChangeActivity.super.finish();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    enableHumanVerification();
                } else if (caught instanceof UserRuntimeException) {
                    MessageDialog.error(i18n.tr("Failed to change password"), caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, getView().getValue());
    }

    protected void enableHumanVerification() {
        if (CaptchaComposite.isPublicKeySet()) {
            getView().enableHumanVerification();
        } else {
            AsyncCallback<String> callback = new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CaptchaComposite.setPublicKey(result);
                    getView().enableHumanVerification();
                }

            };
            ClientContext.getAuthenticationService().obtainRecaptchaPublicKey(callback);
        }
    }
}
