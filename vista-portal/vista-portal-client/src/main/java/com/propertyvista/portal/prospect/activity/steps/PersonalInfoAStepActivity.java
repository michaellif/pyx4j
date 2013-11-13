/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.steps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ui.steps.PersonalInfoAStepView;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.services.PersonalInfoAStepService;
import com.propertyvista.portal.rpc.portal.web.dto.application.PersonalInfoAStepDTO;

public class PersonalInfoAStepActivity extends AbstractProspectWizardStepActivity<PersonalInfoAStepDTO> {

    public PersonalInfoAStepActivity(AppPlace place) {
        super(PersonalInfoAStepView.class, GWT.<PersonalInfoAStepService> create(PersonalInfoAStepService.class));
    }

    @Override
    public void navigateToNextStep() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application.PersonalInfoBStep());
    }

    @Override
    public void navigateToPreviousStep() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application.OptionsStep());
    }

}
