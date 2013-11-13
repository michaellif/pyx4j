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

import com.propertyvista.portal.prospect.ui.steps.PeopleStepView;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.PeopleStepDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.PeopleStepService;

public class PeopleStepActivity extends AbstractProspectWizardStepActivity<PeopleStepDTO> {

    public PeopleStepActivity(AppPlace place) {
        super(PeopleStepView.class, GWT.<PeopleStepService> create(PeopleStepService.class));
    }

    @Override
    public void navigateToNextStep() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application.ContactsStep());
    }

    @Override
    public void navigateToPreviousStep() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application.FinancialStep());
    }

}
