/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.resident.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.portal.resident.activity.QuickTipActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Logout;

public class QuickTipActivityMapper implements ActivityMapper {

    public QuickTipActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (SecurityController.check(VistaAccessGrantedBehavior.ResidentPortal) && !(place instanceof Logout)) {
            return new QuickTipActivity(place);
        } else {
            return null;
        }
    }

}
