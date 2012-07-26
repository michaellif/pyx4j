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
package com.propertyvista.crm.client.activity.crud.customer.guarantor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorListerActivity extends ListerActivityBase<GuarantorDTO> {

    @SuppressWarnings("unchecked")
    public GuarantorListerActivity(Place place) {
        super(place, CustomerViewFactory.instance(GuarantorListerView.class), (AbstractCrudService<GuarantorDTO>) GWT.create(GuarantorCrudService.class),
                GuarantorDTO.class);

        // filter out just current tenants:
        GuarantorDTO proto = EntityFactory.getEntityPrototype(GuarantorDTO.class);
        addPreDefinedFilter(PropertyCriterion.in(proto.leaseV().holder().status(), Lease.Status.current()));
        // and finalize lease only:
        addPreDefinedFilter(PropertyCriterion.isNotNull(proto.leaseV().fromDate()));
        addPreDefinedFilter(PropertyCriterion.isNull(proto.leaseV().toDate()));
    }

    @Override
    public boolean canCreateNewItem() {
        return false; // disable creation of the new stand-alone Guarantor - just from within the Lease!..
    }
}
