/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.PaymentRecordDTO;

public class CollectionsGadgetServiceImpl implements CollectionsGadgetService {

    @Override
    public void countData(AsyncCallback<CollectionsGadgetDataDTO> callback, Vector<Building> queryParams) {
        CollectionsGadgetDataDTO data = EntityFactory.create(CollectionsGadgetDataDTO.class);
        data.tenantsPaidThisMonth().setValue(0);
        data.fundsCollectedThisMonth().setValue(Utils.asMoney(new BigDecimal("55342")));
        data.fundsInProcessing().setValue(Utils.asMoney(new BigDecimal("19924")));
        callback.onSuccess(data);
    }

    @Override
    public void makePaymentCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, Vector<Building> buildingsFilter, String filter) {
        callback.onSuccess(EntityListCriteria.create(PaymentRecordDTO.class));
    }

}
