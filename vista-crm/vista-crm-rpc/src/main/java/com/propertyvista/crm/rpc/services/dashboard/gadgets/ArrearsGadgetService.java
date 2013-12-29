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
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetQueryDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.DelinquentLeaseCriteriaProvider;

public interface ArrearsGadgetService extends AbstractCounterGadgetBaseService<ArrearsGadgetDataDTO, ArrearsGadgetQueryDataDTO>,
        DelinquentLeaseCriteriaProvider {

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, ArrearsGadgetQueryDataDTO queryParams);

    @Override
    public void makeDelinquentLeaseCriteria(AsyncCallback<EntityListCriteria<DelinquentLeaseDTO>> callback, ArrearsGadgetQueryDataDTO query,
            String criteriaPreset);
}
