/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.resident;

import com.google.gwt.core.client.GWT;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.resident.activity.PortalClientCommunicationManager;
import com.propertyvista.portal.resident.themes.ResidentPortalTheme;
import com.propertyvista.portal.resident.ui.ResidentPortalRootPane;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.shared.PortalSite;

public class ResidentPortalSite extends PortalSite {

    public ResidentPortalSite() {
        super(VistaApplication.resident, ResidentPortalSiteMap.class, new ResidentPortalRootPane(), new ResidentPortalSiteDispatcher(),
                new ResidentPortalTheme());
    }

    @Override
    public void onSiteLoad() {
        // RPC creation below, Entity needs to be compiled -> generated first
        ClientContext.setAuthenticationService(GWT.<AuthenticationService> create(ResidentAuthenticationService.class));
        super.onSiteLoad();

        PortalClientCommunicationManager.instance();
    }

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new PortalSiteMap.NotificationPlace();
        place.setNotification(notification);
        return place;
    }
}
