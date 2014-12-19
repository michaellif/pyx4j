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
 */
package com.propertyvista.portal.shared.activity.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordResetService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPasswordResetService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;
import com.propertyvista.portal.shared.ui.security.PasswordResetWizardView;
import com.propertyvista.portal.shared.ui.security.PasswordResetWizardView.PasswordResetWizardPresenter;

public class PasswordResetActivity extends AbstractWizardActivity<PasswordChangeRequest, PasswordResetWizardView> implements PasswordResetWizardPresenter {

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
    public void finish() {
        GWT.<AbstractPasswordResetService> create(PortalPasswordResetService.class).resetPassword(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                getView().reset();
                Notification message = new Notification(null, i18n.tr("Your password has been reset successfully!"), NotificationType.INFO);
                PortalSite.getPlaceController().showNotification(message);
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

    @Override
    public String mayStop() {
        return null;
    };

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
    }

}
