/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.resident.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.activity.PortalMenuActivity;
import com.propertyvista.portal.resident.activity.movein.MoveInWizardMenuActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Logout;

public class MenuActivityMapper implements ActivityMapper {

    public MenuActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated() && !(place instanceof Logout)) {
            if (SecurityController.check(PortalResidentBehavior.LeaseAgreementSigningRequired)) {
                return new MoveInWizardMenuActivity(place);
            } else {
                return new PortalMenuActivity(place);
            }
        } else {
            return null;
        }
    }
}
