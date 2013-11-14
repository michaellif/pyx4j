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

import com.pyx4j.security.rpc.AuthenticationService;

import com.propertyvista.portal.prospect.themes.ProspectPortalTheme;
import com.propertyvista.portal.prospect.ui.ProspectPortalRootPane;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.services.ProspectAuthenticationService;
import com.propertyvista.portal.shared.PortalSite;

public class ProspectPortalSite extends PortalSite {

    public ProspectPortalSite() {
        super("vista-prospect", ProspectPortalSiteMap.class, new ProspectPortalRootPane(), new ProspectPortalSiteDispatcher(), new ProspectPortalTheme());
    }

    @Override
    protected AuthenticationService getAuthenticationService() {
        return GWT.create(ProspectAuthenticationService.class);
    }

}
