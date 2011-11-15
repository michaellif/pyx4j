/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.info.InfoView;
import com.propertyvista.portal.ptapp.client.ui.steps.info.InfoViewPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;

public class InfoActivity extends WizardStepWithSubstepsActivity<TenantInfoDTO, InfoViewPresenter> implements InfoViewPresenter {

    public InfoActivity(AppPlace place) {
        super((InfoView) WizardStepsViewFactory.instance(InfoView.class), TenantInfoDTO.class, (TenantInfoService) GWT.create(TenantInfoService.class));
        withPlace(place);
    }
}
