/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.portal.prospect.activity.BreadcrumbsActivity;

public class BreadcrumbsActivityMapper implements ActivityMapper {

    public BreadcrumbsActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        return new BreadcrumbsActivity(place);
    }

}
