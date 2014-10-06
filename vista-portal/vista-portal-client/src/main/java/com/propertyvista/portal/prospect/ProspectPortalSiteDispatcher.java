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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.shared.config.VistaFeatures;

public class ProspectPortalSiteDispatcher extends AbstractAppPlaceDispatcher {

    private static final I18n i18n = I18n.get(ProspectPortalSiteDispatcher.class);

    @Override
    protected AppPlace obtainDefaultPlace() {
        if (ClientContext.isAuthenticated()) {
            return new ProspectPortalSiteMap.Status();
        } else {
            return new PortalSiteMap.Login();
        }
    }

    @Override
    protected boolean isPlaceNavigable(AppPlace targetPlace) {
        if (targetPlace instanceof ProspectPortalSiteMap.ApplicationContextSelection) {
            return SecurityController.check(PortalProspectBehavior.ApplicationSelectionRequired, PortalProspectBehavior.HasMultipleApplications);
        } else {
            return true;
        }
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        if (!VistaFeatures.instance().onlineApplication()) {
            Notification message = new Notification(null, i18n.tr("We're sorry, this site has not been activated yet."), NotificationType.INFO);
            PortalSiteMap.NotificationPlace place = new PortalSiteMap.NotificationPlace();
            place.setNotification(message);
            return place;
        }

        if (!(newPlace instanceof PublicPlace) && !ClientContext.isAuthenticated()) {
            return new PortalSiteMap.Login();
        }
        if (newPlace instanceof PortalSiteMap.Logout) {
            return newPlace;
        }

        if (SecurityController.check(VistaBasicBehavior.ProspectPortalPasswordChangeRequired)) {
            return new PortalSiteMap.PasswordReset();
        } else if (SecurityController.check(PortalProspectBehavior.ApplicationSelectionRequired)) {
            return new ProspectPortalSiteMap.ApplicationContextSelection();
        } else {
            return newPlace;
        }
    }

}
