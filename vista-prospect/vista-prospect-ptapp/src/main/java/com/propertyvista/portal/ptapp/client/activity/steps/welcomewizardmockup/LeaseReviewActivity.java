/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.moveinwizardmockup.LeaseReviewDTO;
import com.propertyvista.portal.ptapp.client.activity.steps.WizardStepActivity;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.reviewlease.LeaseReviewPresenter;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.reviewlease.LeaseReviewView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.LeaseReviewService;

public class LeaseReviewActivity extends WizardStepActivity<LeaseReviewDTO, LeaseReviewPresenter> implements LeaseReviewPresenter {

    public LeaseReviewActivity(AppPlace place) {
        super(WizardStepsViewFactory.instance(LeaseReviewView.class), LeaseReviewDTO.class, GWT.<LeaseReviewService> create(LeaseReviewService.class));
        withPlace(place);
    }

}
