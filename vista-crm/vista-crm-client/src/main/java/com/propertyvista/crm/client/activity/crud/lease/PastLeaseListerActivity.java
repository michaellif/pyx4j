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
package com.propertyvista.crm.client.activity.crud.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.lease.PastLeaseListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class PastLeaseListerActivity extends ListerActivityBase<LeaseDTO> {

    @SuppressWarnings("unchecked")
    public PastLeaseListerActivity(Place place) {
        super(place, LeaseViewFactory.instance(PastLeaseListerView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseViewerCrudService.class), LeaseDTO.class);

        LeaseDTO proto = EntityFactory.getEntityPrototype(LeaseDTO.class);
        addPreDefinedFilter(PropertyCriterion.in(proto.version().status(), Lease.Status.former()));
        // and current lease version only:
        addPreDefinedFilter(PropertyCriterion.isNotNull(proto.version().fromDate()));
        addPreDefinedFilter(PropertyCriterion.isNull(proto.version().toDate()));
    }
}
