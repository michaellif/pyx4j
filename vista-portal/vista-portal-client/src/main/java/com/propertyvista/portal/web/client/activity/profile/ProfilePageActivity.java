/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.profile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.profile.ProfilePageView;
import com.propertyvista.portal.web.client.ui.profile.ProfilePageView.ProfilePagePresenter;

public class ProfilePageActivity extends AbstractEditorActivity<ResidentDTO> implements ProfilePagePresenter {

    public ProfilePageActivity(AppPlace place) {
        super(ProfilePageView.class, GWT.<PersonalInfoCrudService> create(PersonalInfoCrudService.class), place);
    }

}
