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

import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.client.AppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.portal.rpc.ptapp.AuthPublicPlace;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.WizardStepPlace;

public class PtAppPlaceDispatcher extends AppPlaceDispatcher {

    @Override
    public void forwardTo(AppPlace newPlace, AsyncCallback<AppPlace> callback) {

        if (ClientSecurityController.checkBehavior(VistaBehavior.PROSPECTIVE_TENANT) || ClientSecurityController.checkBehavior(VistaBehavior.GUARANTOR)) {
            if (newPlace instanceof WizardStepPlace) {
                PtAppSite.getWizardManager().forwardTo(newPlace, callback);
                return;
            } else if (newPlace instanceof AuthPublicPlace) {
                PtAppSite.getWizardManager().forwardTo(null, callback);
                return;
            }
        } else {
            if (!(newPlace instanceof AuthPublicPlace)) {
                callback.onSuccess(new PtSiteMap.Login());
                return;
            }
        }

        callback.onSuccess(newPlace);
    }
}
