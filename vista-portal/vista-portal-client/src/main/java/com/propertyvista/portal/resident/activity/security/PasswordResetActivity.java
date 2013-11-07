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
package com.propertyvista.portal.resident.activity.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AbstractPasswordResetService;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.security.PasswordResetWizardView;
import com.propertyvista.portal.resident.ui.security.PasswordResetWizardView.PasswordResetWizardPresenter;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;

public class PasswordResetActivity extends AbstractWizardActivity<PasswordChangeRequest> implements PasswordResetWizardPresenter {

    private static final I18n i18n = I18n.get(AbstractPasswordResetActivity.class);

    public PasswordResetActivity(Place place) {
        super(PasswordResetWizardView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        getView().populate(EntityFactory.create(PasswordChangeRequest.class));
    }

    @Override
    protected void onFinish() {
        Notification message = new Notification(null, i18n.tr("Your password has been reset successfully!"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message);
    }

    @Override
    public void submit() {
        GWT.<AbstractPasswordResetService> create(PortalPasswordResetService.class).resetPassword(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                getView().reset();
                ClientContext.authenticated(result);
                PasswordResetActivity.super.submit();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    MessageDialog.error(i18n.tr("Failed to change password"), caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, getView().getValue());
    }

}
