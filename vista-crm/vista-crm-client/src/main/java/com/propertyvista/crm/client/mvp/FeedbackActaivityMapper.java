/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.client.activity.FeedbackActivity;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;

public class FeedbackActaivityMapper implements ActivityMapper {

    private final FeedbackActivity activity = new FeedbackActivity();

    public FeedbackActaivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {

        if (SecurityController.check(VistaAccessGrantedBehavior.CRM)) {
            return activity.withPlace(place);
        } else {
            return null;
        }
    }
}
