/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class PtAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected boolean isApplicationAuthenticated() {
        return SecurityController.checkBehavior(VistaBasicBehavior.ProspectiveApp);
    }

    @Override
    protected void obtainDefaulPublicPlacePlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new PtSiteMap.Login());
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        PtAppSite.getWizardManager().isPlaceNavigable(targetPlace, callback);
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        PtAppSite.getWizardManager().obtainPlace(callback);
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.ProspectiveAppPasswordChangeRequired)) {
            return new PtSiteMap.PasswordReset();
        } else {
            return null;
        }
    }
}
