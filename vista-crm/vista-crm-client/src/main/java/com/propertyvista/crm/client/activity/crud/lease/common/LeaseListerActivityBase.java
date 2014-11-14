/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.common;

import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.activity.AbstractListerActivity;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.LeaseDTO;

public class LeaseListerActivityBase<DTO extends LeaseDTO> extends AbstractListerActivity<DTO> {

    public LeaseListerActivityBase(Place place, IListerView<DTO> view, Class<DTO> entityClass) {
        super(entityClass, place, view);
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<DTO> entityClass, EntityFiltersBuilder<DTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;
        if ((val = place.getFirstArg(filters.proto().leaseParticipants().$().customer().customerId().getPath().toString())) != null) {
            filters.eq(filters.proto().leaseParticipants().$().customer().customerId(), val);
        }
    }
}
