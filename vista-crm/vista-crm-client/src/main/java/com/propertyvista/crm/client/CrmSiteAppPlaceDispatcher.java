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

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class CrmSiteAppPlaceDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected boolean isPlaceNavigable(AppPlace targetPlace) {
        // TODO security for places
        return true;
    }

    @Override
    protected AppPlace obtainDefaultPlace() {
        if (ClientContext.isAuthenticated()) {
            return CrmSite.getSystemDashboardPlace();
        } else {
            return new CrmSiteMap.Login();
        }
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        if (!(newPlace instanceof PublicPlace) && !ClientContext.isAuthenticated()) {
            return new CrmSiteMap.Login();
        }
        if (SecurityController.check(VistaBasicBehavior.CRMPasswordChangeRequired)) {
            return new CrmSiteMap.PasswordReset();
        } else if (SecurityController.check(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired)) {
            return new CrmSiteMap.Account.AccountRecoveryOptionsRequired();
        } else {
            return newPlace;
        }
    }

}
