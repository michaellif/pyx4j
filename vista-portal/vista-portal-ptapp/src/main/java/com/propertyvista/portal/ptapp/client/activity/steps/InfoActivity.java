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

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.info.InfoView;
import com.propertyvista.portal.ptapp.client.ui.steps.info.InfoViewPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.steps.AbstractWizardService;
import com.propertyvista.portal.rpc.ptapp.services.steps.GuarantorInfoService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantInfoService;

public class InfoActivity extends WizardStepWithSubstepsActivity<TenantInfoDTO, InfoViewPresenter> implements InfoViewPresenter {

    public InfoActivity(AppPlace place) {
        super(WizardStepsViewFactory.instance(InfoView.class), TenantInfoDTO.class, createaService());
        withPlace(place);
    }

    @SuppressWarnings("unchecked")
    private static AbstractWizardService<TenantInfoDTO> createaService() {

        if (SecurityController.checkBehavior(VistaTenantBehavior.Guarantor)) {
            return (AbstractWizardService<TenantInfoDTO>) GWT.create(GuarantorInfoService.class);
        } else {
            return (AbstractWizardService<TenantInfoDTO>) GWT.create(TenantInfoService.class);
        }
    }
}
