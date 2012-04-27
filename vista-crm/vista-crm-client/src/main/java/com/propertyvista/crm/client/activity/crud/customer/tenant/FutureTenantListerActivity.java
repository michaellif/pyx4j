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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.customer.tenant.FutureTenantListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TenantDTO;

public class FutureTenantListerActivity extends ListerActivityBase<TenantDTO> {

    @SuppressWarnings("unchecked")
    public FutureTenantListerActivity(Place place) {
        super(place, MarketingViewFactory.instance(FutureTenantListerView.class), (AbstractCrudService<TenantDTO>) GWT.create(TenantCrudService.class),
                TenantDTO.class);

        // filter out just current tenants:
        TenantDTO proto = EntityFactory.getEntityPrototype(TenantDTO.class);
        addPreDefinedFilter(PropertyCriterion.in(proto.leaseV().status(), Lease.Status.draft()));
        // restrict to draft lease version only        
        addPreDefinedFilter(PropertyCriterion.isNull(proto.leaseV().fromDate()));
        addPreDefinedFilter(PropertyCriterion.isNull(proto.leaseV().toDate()));
    }

    @Override
    public boolean canEditNew() {
//        return SecurityController.checkBehavior(VistaCrmBehavior.Tenants);
        return false; // disable creation of the new stand-alone Tenant - just from within the Lease!..
    }
}
