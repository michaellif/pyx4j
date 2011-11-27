/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.client.AppPlaceDispatcher;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.VistaBehavior;

public class CrmSiteAppPlaceDispatcher extends AppPlaceDispatcher {

    protected static I18n i18n = I18n.get(CrmSiteAppPlaceDispatcher.class);

    private AppPlace targetPlace = AppPlace.NOWHERE;

    @Override
    public void forwardTo(AppPlace newPlace, AsyncCallback<AppPlace> callback) {
        if (newPlace instanceof PublicPlace) {
            callback.onSuccess(newPlace);
        } else if (isAuthenticated()) {
            if (newPlace == AppPlace.NOWHERE) {
                if (targetPlace != AppPlace.NOWHERE) {
                    AppSite.getPlaceController().goTo(targetPlace);
                } else {
                    AppSite.getPlaceController().goTo(getDefaultAuthenticatedPlace());
                }
            } else {
                callback.onSuccess(newPlace);
            }
            targetPlace = AppPlace.NOWHERE;
        } else {
            targetPlace = newPlace;
            AppSite.getPlaceController().goTo(getDefaulPublicPlacePlace());
        }
    }

    public boolean isAuthenticated() {
        return ClientContext.isAuthenticated() && ClientSecurityController.checkAnyBehavior(VistaBehavior.getCrmBehaviors());
    }

    protected AppPlace getDefaulPublicPlacePlace() {
        return new CrmSiteMap.Login();
    }

    protected AppPlace getDefaultAuthenticatedPlace() {
        return CrmSite.getSystemFashboardPlace();
    }

    @Override
    public void confirm(String message, Command onConfirmed) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Are you sure you want to navigate away from this page?\n\n" + "{0}\n\n"
                + "Press Yes to continue, or No to stay on the current page.", message), onConfirmed);
    }

}
