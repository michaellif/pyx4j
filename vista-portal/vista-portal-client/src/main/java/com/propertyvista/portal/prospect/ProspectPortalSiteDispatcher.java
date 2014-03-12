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
package com.propertyvista.portal.prospect;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.shared.config.VistaFeatures;

public class ProspectPortalSiteDispatcher extends AbstractAppPlaceDispatcher {

    private static final I18n i18n = I18n.get(ProspectPortalSiteDispatcher.class);

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new PortalSiteMap.NotificationPlace();
        place.setNotification(notification);
        return place;
    }

    @Override
    protected void obtainDefaultPlace(AsyncCallback<AppPlace> callback) {
        if (ClientContext.isAuthenticated()) {
            callback.onSuccess(new ProspectPortalSiteMap.Status());
        } else {
            callback.onSuccess(new PortalSiteMap.Login());
        }
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        if (targetPlace instanceof ProspectPortalSiteMap.ApplicationContextSelection) {
            callback.onSuccess(SecurityController.checkAnyBehavior(PortalProspectBehavior.HasMultipleApplications));
        } else {
            callback.onSuccess(Boolean.TRUE);
        }
    }

    @Override
    protected AppPlace mandatoryPublicActionForward(AppPlace newPlace) {
        if (!VistaFeatures.instance().onlineApplication()) {
            Notification message = new Notification(null, i18n.tr("We're sorry, this site has not been activated yet."), NotificationType.INFO);
            PortalSiteMap.NotificationPlace place = new PortalSiteMap.NotificationPlace();
            place.setNotification(message);
            return place;
        } else {
            return super.mandatoryPublicActionForward(newPlace);
        }
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.ProspectPortalPasswordChangeRequired)) {
            return new PortalSiteMap.PasswordReset();
        } else if (SecurityController.checkBehavior(PortalProspectBehavior.ApplicationSelectionRequired)) {
            return new ProspectPortalSiteMap.ApplicationContextSelection();
        } else {
            return null;
        }
    }

}
