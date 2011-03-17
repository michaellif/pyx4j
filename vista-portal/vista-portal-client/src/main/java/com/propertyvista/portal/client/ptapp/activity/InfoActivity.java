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
import com.propertyvista.portal.client.ptapp.ui.InfoView;
import com.propertyvista.portal.client.ptapp.ui.InfoViewPresenter;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.rpc.pt.services.TenantsInfoServices;

public class InfoActivity extends WizardStepWithSubstepsActivity<PotentialTenantInfo, InfoViewPresenter> implements InfoViewPresenter {

    @Inject
    public InfoActivity(InfoView view) {
        super(view, PotentialTenantInfo.class, (TenantsInfoServices) GWT.create(TenantsInfoServices.class));
    }
}
