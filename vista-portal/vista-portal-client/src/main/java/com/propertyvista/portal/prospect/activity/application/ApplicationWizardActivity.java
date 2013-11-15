/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.application;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Financial;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class ApplicationWizardActivity extends AbstractWizardCrudActivity<ApplicationDTO> implements ApplicationWizardPresenter {

    public ApplicationWizardActivity(AppPlace place) {
        super(ApplicationWizardView.class, GWT.<ApplicationWizardService> create(ApplicationWizardService.class), ApplicationDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted(result));
    }

}