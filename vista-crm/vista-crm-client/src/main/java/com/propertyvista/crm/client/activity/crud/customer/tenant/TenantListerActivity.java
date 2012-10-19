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

import com.pyx4j.entity.shared.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TenantDTO;

public class TenantListerActivity extends ListerActivityBase<TenantDTO> {

    public TenantListerActivity(Place place) {
        super(place, CustomerViewFactory.instance(TenantListerView.class), GWT.<TenantCrudService> create(TenantCrudService.class), TenantDTO.class);

        // filter out just current tenants:

        EntityFiltersBuilder<TenantDTO> filters = EntityFiltersBuilder.create(TenantDTO.class);
        filters.in(filters.proto().lease().status(), Lease.Status.current());
        filters.eq(filters.proto().leaseParticipants().$().leaseTermV().holder(), filters.proto().lease().currentTerm());
        // and finalized e.g. last only:
        filters.isCurrent(filters.proto().leaseParticipants().$().leaseTermV());

        addPreDefinedFilter(filters);
    }

    @Override
    public boolean canCreateNewItem() {
        return false; // disable creation of the new stand-alone Tenant - just from within the Lease!.. 
    }
}
