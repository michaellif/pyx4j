/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class CrmSiteAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    private static final I18n i18n = I18n.get(CrmSiteAppPlaceDispatcher.class);

    @Override
    protected void obtainDefaulPublicPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new CrmSiteMap.Login());
    }

    @Override
    protected boolean isApplicationAuthenticated() {
        return SecurityController.checkBehavior(VistaBasicBehavior.CRM);
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        // TODO security for places
        callback.onSuccess(Boolean.TRUE);
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(CrmSite.getSystemDashboardPlace());
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.CRMPasswordChangeRequired)) {
            return new CrmSiteMap.PasswordReset();
        } else if (SecurityController.checkBehavior(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired)) {
            return new CrmSiteMap.Account.AccountRecoveryOptionsRequired();
        } else {
            return null;
        }
    }

    @Override
    public NotificationAppPlace getUserMessagePlace(Notification notification) {
        NotificationAppPlace place = new CrmSiteMap.RuntimeError();
        place.setNotification(notification);
        return place;
    }

    @Override
    public void confirm(String message, ConfirmDecline confirmDecline) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Are you sure you want to navigate away from this page?\n\n" + "{0}\n\n"
                + "Press Yes to continue, or No to stay on the current page.", message), confirmDecline);
    }

}
