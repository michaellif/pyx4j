/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TenantDTO;

public class ArrearsGadgetServiceImpl implements ArrearsGadgetService {

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, Vector<Building> queryParams) {
        ArrearsGadgetDataDTO data = EntityFactory.create(ArrearsGadgetDataDTO.class);
        data.delinquentTenants().setValue(9001);

        data.bucketThisMonth().setValue(new BigDecimal("5.99"));
        data.buckets().bucket30().setValue(new BigDecimal("199.99"));
        data.buckets().bucket60().setValue(new BigDecimal("299.99"));
        data.buckets().bucket90().setValue(new BigDecimal("399.99"));
        data.buckets().bucketOver90().setValue(new BigDecimal("1399.99"));

        callback.onSuccess(data);
    }

    @Override
    public void makeTenantCriteria(AsyncCallback<EntityListCriteria<TenantDTO>> callback, Vector<Building> buildingsFilter, String criteriaPreset) {
        callback.onSuccess(EntityListCriteria.create(TenantDTO.class));
    }

}
