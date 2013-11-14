/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class ResidentPortalSiteDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new PortalSiteMap.NotificationPlace();
        place.setNotification(notification);
        return place;
    }

    @Override
    protected void obtainDefaultPlace(AsyncCallback<AppPlace> callback) {
        if (ClientContext.isAuthenticated()) {
            callback.onSuccess(new ResidentPortalSiteMap.Dashboard());
        } else {
            callback.onSuccess(new PortalSiteMap.Login());
        }
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        if (targetPlace instanceof ResidentPortalSiteMap.LeaseContextSelection) {
            callback.onSuccess(SecurityController.checkAnyBehavior(VistaCustomerBehavior.LeaseSelectionRequired, VistaCustomerBehavior.HasMultipleLeases));
        } else {
            callback.onSuccess(Boolean.TRUE);
        }
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.ResidentPortalPasswordChangeRequired)) {
            return new PortalSiteMap.PasswordReset();
        } else if (SecurityController.checkBehavior(VistaCustomerBehavior.LeaseSelectionRequired)) {
            return new ResidentPortalSiteMap.LeaseContextSelection();
        } else {
            return null;
        }
    }

}
