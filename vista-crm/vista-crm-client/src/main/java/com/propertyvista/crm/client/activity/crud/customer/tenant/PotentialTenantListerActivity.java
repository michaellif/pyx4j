/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.customer.tenant.PotentialTenantListerView;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.dto.TenantDTO;

public class PotentialTenantListerActivity extends AbstractListerActivity<TenantDTO> {

    public PotentialTenantListerActivity(Place place) {
        super(place, CrmSite.getViewFactory().getView(PotentialTenantListerView.class), GWT.<TenantCrudService> create(TenantCrudService.class),
                TenantDTO.class);
    }

    @Override
    public boolean canCreateNewItem() {
        return false; // disable creation of the new stand-alone Tenant - just from within the Lease!..
    }
}
