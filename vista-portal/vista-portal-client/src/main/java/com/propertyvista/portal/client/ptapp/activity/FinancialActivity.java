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
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.FinancialView;
import com.propertyvista.portal.client.ptapp.ui.FinancialViewPresenter;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.rpc.pt.services.TenantsFinancialServices;

public class FinancialActivity extends WizardStepActivity<PotentialTenantFinancial, FinancialViewPresenter> implements FinancialViewPresenter {

    @Inject
    public FinancialActivity(FinancialView view) {
        super(view, PotentialTenantFinancial.class, (TenantsFinancialServices) GWT.create(TenantsFinancialServices.class));
    }
}
