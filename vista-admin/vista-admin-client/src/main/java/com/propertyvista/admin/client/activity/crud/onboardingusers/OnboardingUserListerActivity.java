/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.onboardingusers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.admin.client.ui.crud.onboardingusers.OnboardingUserListerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.OnboardingUserDTO;
import com.propertyvista.admin.rpc.services.OnboardingUserCrudService;

public class OnboardingUserListerActivity extends ListerActivityBase<OnboardingUserDTO> {

    public OnboardingUserListerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(OnboardingUserListerView.class), GWT.<OnboardingUserCrudService> create(OnboardingUserCrudService.class),
                OnboardingUserDTO.class);
    }

}
