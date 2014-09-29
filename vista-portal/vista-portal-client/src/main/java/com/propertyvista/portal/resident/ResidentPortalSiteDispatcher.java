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
package com.propertyvista.portal.resident;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AbstractAppPlaceDispatcher;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class ResidentPortalSiteDispatcher extends AbstractAppPlaceDispatcher {

    @Override
    protected AppPlace obtainDefaultPlace() {
        if (ClientContext.isAuthenticated()) {
            return new ResidentPortalSiteMap.Dashboard();
        } else {
            return new PortalSiteMap.Login();
        }
    }

    @Override
    protected boolean isPlaceNavigable(AppPlace targetPlace) {
        if (targetPlace instanceof ResidentPortalSiteMap.LeaseContextSelection) {
            return SecurityController.check(PortalResidentBehavior.LeaseSelectionRequired, PortalResidentBehavior.HasMultipleLeases);
        } else {
            return true;
        }
    }

    @Override
    protected AppPlace mandatoryActionForward(AppPlace newPlace) {
        if (!(newPlace instanceof PublicPlace) && !ClientContext.isAuthenticated()) {
            return new PortalSiteMap.Login();
        }
        if (newPlace instanceof PortalSiteMap.Logout) {
            return newPlace;
        }
        if (SecurityController.check(VistaBasicBehavior.ResidentPortalPasswordChangeRequired)) {
            return new PortalSiteMap.PasswordReset();
        } else if (SecurityController.check(PortalResidentBehavior.LeaseSelectionRequired)) {
            return new ResidentPortalSiteMap.LeaseContextSelection();
        } else if (SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionRequired)) {
            if (newPlace == AppPlace.NOWHERE) {
                return new ResidentPortalSiteMap.MoveIn.MoveInWizard();
            }
        }
        return newPlace;
    }

}
