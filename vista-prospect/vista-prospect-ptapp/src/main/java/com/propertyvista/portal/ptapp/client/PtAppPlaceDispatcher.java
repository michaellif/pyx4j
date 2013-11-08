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

import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class PtAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected void obtainDefaulPublicPlace(AsyncCallback<AppPlace> callback) {
        callback.onSuccess(new PtSiteMap.Login());
    }

    @Override
    protected void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback) {
        if (targetPlace instanceof PtSiteMap.PasswordChange) {
            callback.onSuccess(Boolean.TRUE);
        } else if (targetPlace instanceof PtSiteMap.ApplicationSelectionRequired) {
            callback.onSuccess(SecurityController.checkAnyBehavior(VistaCustomerBehavior.ApplicationSelectionRequired,
                    VistaCustomerBehavior.HasMultipleApplications));
        } else {
            PtAppSite.getWizardManager().isPlaceNavigable(targetPlace, callback);
        }
    }

    @Override
    protected void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback) {
        PtAppSite.getWizardManager().obtainPlace(callback);
    }

    @Override
    protected AppPlace specialForward(AppPlace newPlace) {
        if (SecurityController.checkBehavior(VistaBasicBehavior.ProspectivePortalPasswordChangeRequired)) {
            return new PtSiteMap.PasswordReset();
        } else if (SecurityController.checkBehavior(VistaCustomerBehavior.ApplicationSelectionRequired)) {
            return new PtSiteMap.ApplicationSelectionRequired();
        } else {
            return null;
        }
    }
}
