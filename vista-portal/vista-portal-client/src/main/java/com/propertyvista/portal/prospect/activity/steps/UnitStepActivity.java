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

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.ui.steps.UnitStepView;
import com.propertyvista.portal.rpc.portal.services.UnitStepService;
import com.propertyvista.portal.rpc.portal.web.dto.application.UnitInfoDTO;
import com.propertyvista.portal.shared.activity.AbstractWizardStepActivity;

public class UnitStepActivity extends AbstractWizardStepActivity<UnitInfoDTO> {

    public UnitStepActivity(AppPlace place) {
        super(UnitStepView.class, GWT.<UnitStepService> create(UnitStepService.class));
        // TODO Auto-generated constructor stub
    }

    @Override
    public void navigateOut() {
        // TODO Auto-generated method stub

    }

    @Override
    public void navigateToNextStep() {
        // TODO Auto-generated method stub

    }

    @Override
    public void navigateToPreviousStep() {
        // TODO Auto-generated method stub

    }

}
