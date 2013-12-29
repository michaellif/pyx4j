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
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.LeaseCriteriaProvider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.PaymentCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface CollectionsGadgetService extends AbstractCounterGadgetBaseService<CollectionsGadgetDataDTO, Vector<Building>>, PaymentCriteriaProvider,
        LeaseCriteriaProvider {

    @Override
    public void countData(AsyncCallback<CollectionsGadgetDataDTO> callback, Vector<Building> queryParams);

    @Override
    public void makePaymentCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, Vector<Building> buildingsFilter, String filter);

    @Override
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter, String leaseFilter);

}
