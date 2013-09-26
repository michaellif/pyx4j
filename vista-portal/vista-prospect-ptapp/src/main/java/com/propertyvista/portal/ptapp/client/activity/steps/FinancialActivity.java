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

import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.financial.FinancialView;
import com.propertyvista.portal.ptapp.client.ui.steps.financial.FinancialViewPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.steps.AbstractWizardService;
import com.propertyvista.portal.rpc.ptapp.services.steps.GuarantorFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantFinancialService;

public class FinancialActivity extends WizardStepWithSubstepsActivity<TenantFinancialDTO, FinancialViewPresenter> implements FinancialViewPresenter {

    public FinancialActivity(AppPlace place) {
        super(WizardStepsViewFactory.instance(FinancialView.class), TenantFinancialDTO.class, createaService());
        withPlace(place);
    }

    @SuppressWarnings("unchecked")
    private static AbstractWizardService<TenantFinancialDTO> createaService() {

        if (SecurityController.checkBehavior(VistaCustomerBehavior.Guarantor)) {
            return (AbstractWizardService<TenantFinancialDTO>) GWT.create(GuarantorFinancialService.class);
        } else {
            return (AbstractWizardService<TenantFinancialDTO>) GWT.create(TenantFinancialService.class);
        }
    }
}
