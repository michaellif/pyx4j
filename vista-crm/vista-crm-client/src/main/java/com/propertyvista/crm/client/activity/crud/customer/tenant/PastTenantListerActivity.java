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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.customer.tenant.PastTenantListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TenantDTO;

public class PastTenantListerActivity extends ListerActivityBase<TenantDTO> {

    public PastTenantListerActivity(Place place) {
        super(place, CustomerViewFactory.instance(PastTenantListerView.class), GWT.<TenantCrudService> create(TenantCrudService.class), TenantDTO.class);

        // filter out just former tenants:
        TenantDTO proto = EntityFactory.getEntityPrototype(TenantDTO.class);

        OrCriterion or = new OrCriterion();
        or.left(PropertyCriterion.in(proto.lease().status(), Lease.Status.former()));

        or.right(PropertyCriterion.notExists(proto.leaseParticipants().$().leaseTermV()));
        or.right(PropertyCriterion.eq(proto.leaseParticipants().$().leaseTermV().holder(), proto.lease().currentTerm()));
        // and finalized e.g. last only:
        or.right(PropertyCriterion.isNotNull(proto.leaseParticipants().$().leaseTermV().fromDate()));
        or.right(PropertyCriterion.isNull(proto.leaseParticipants().$().leaseTermV().toDate()));

        addPreDefinedFilter(or);

    }

    @Override
    public boolean canCreateNewItem() {
        return false; // disable creation of the new stand-alone Tenant - just from within the Lease!..
    }
}
