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
package com.propertyvista.portal.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class PortalSiteDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected void obtainDefaulPublicPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new PortalSiteMap.Login());
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new PortalSiteMap.Residents());
    }

    @Override
    protected boolean isApplicationAuthenticated() {
        return SecurityController.checkBehavior(VistaBasicBehavior.TenantPortal);
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        if (targetPlace instanceof PortalSiteMap.LeaseContextSelection) {
            callback.onSuccess(Boolean.TRUE);
        } else {
            callback.onSuccess(Boolean.TRUE);
        }
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaCustomerBehavior.LeaseSelectionRequired)) {
            return new PortalSiteMap.LeaseContextSelection();
        } else {
            return null;
        }
    }

}
