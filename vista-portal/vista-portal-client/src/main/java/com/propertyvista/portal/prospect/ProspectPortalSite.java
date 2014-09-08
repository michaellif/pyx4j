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
package com.propertyvista.portal.prospect;

import com.google.gwt.core.client.GWT;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.prospect.themes.ProspectPortalTheme;
import com.propertyvista.portal.prospect.ui.ProspectPortalRootPane;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.shared.PortalSite;

public class ProspectPortalSite extends PortalSite {

    public ProspectPortalSite() {
        super(VistaApplication.prospect, ProspectPortalSiteMap.class, new ProspectPortalRootPane(), new ProspectPortalSiteDispatcher(),
                new ProspectPortalTheme());
    }

    @Override
    public void onSiteLoad() {
        // RPC creation below, Entity needs to be compiled -> generated first
        ClientContext.setAuthenticationService(GWT.<AuthenticationService> create(ProspectAuthenticationService.class));
        super.onSiteLoad();
    }

    @Override
    public NotificationAppPlace getNotificationPlace(Notification notification) {
        NotificationAppPlace place = new PortalSiteMap.NotificationPlace();
        place.setNotification(notification);
        return place;
    }
}
