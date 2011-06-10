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
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.activity.login.LoginActivity;
import com.propertyvista.crm.client.activity.login.RetrievePasswordActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class EntryPointActivityMapper implements ActivityMapper {

    public EntryPointActivityMapper() {
        super();
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof CrmSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof CrmSiteMap.RetrievePassword) {
            return new RetrievePasswordActivity(place);
        }
        return null;
    }
}
