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

import com.propertyvista.portal.prospect.ui.steps.PaymentStepView;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.PaymentStepDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.PaymentStepService;

public class PaymentStepActivity extends AbstractProspectWizardStepActivity<PaymentStepDTO> {

    public PaymentStepActivity(AppPlace place) {
        super(PaymentStepView.class, GWT.<PaymentStepService> create(PaymentStepService.class));
    }

    @Override
    public void navigateToNextStep() {
    }

    @Override
    public void navigateToPreviousStep() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application.SummaryStep());
    }

}
