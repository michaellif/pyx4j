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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.TenantsView;
import com.propertyvista.portal.client.ptapp.ui.TenantsViewPresenter;
import com.propertyvista.portal.domain.pt.PotentialTenantList;

public class TenantsActivity extends WizardStepActivity<PotentialTenantList, TenantsViewPresenter> implements TenantsViewPresenter {

    @Inject
    public TenantsActivity(TenantsView view) {
        super(view, PotentialTenantList.class);
    }
}
