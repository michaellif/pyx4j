/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.util.TransientPropertySortEngine;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrear;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenant;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenantsArrearsDTO;

public class ArrearsReportServiceImpl implements ArrearsReportService {
    private static TransientPropertySortEngine<MockupTenantsArrearsDTO> TRANSIENT_PROPERTY_SORT_ENGINE = new TransientPropertySortEngine<MockupTenantsArrearsDTO>(
            MockupTenantsArrearsDTO.class);

    @Override
    public void arrearsList(AsyncCallback<EntitySearchResult<MockupTenantsArrearsDTO>> callback, Vector<Key> buildings, LogicalDate when,
            Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {
        try {
            // retrieve all the mockup tenants that present in building on the specified date
            // then:
            //  for each tenant compute the arrears
            //  sort the results
            // choose the list for the requested page
            List<Sort> transientSortCriteria = TRANSIENT_PROPERTY_SORT_ENGINE.extractSortCriteriaForTransientProperties(sortingCriteria);
            EntityListCriteria<MockupTenant> criteria = new EntityListCriteria<MockupTenant>(MockupTenant.class);
            criteria.setSorts(sortingCriteria);
            criteria.add(PropertyCriterion.in(criteria.proto().belongsTo().belongsTo().id(), buildings));
            criteria.add(new PropertyCriterion(criteria.proto().moveIn(), Restriction.GREATER_THAN_OR_EQUAL, when));
            criteria.add(new PropertyCriterion(criteria.proto().moveOut(), Restriction.LESS_THAN_OR_EQUAL, when));
            final List<MockupTenant> tenants = Persistence.service().query(criteria);
            PriorityQueue<MockupTenantsArrearsDTO> preliminaryResults = new PriorityQueue<MockupTenantsArrearsDTO>(tenants.size(),
                    TRANSIENT_PROPERTY_SORT_ENGINE.getComparator(transientSortCriteria));
            for (MockupTenant tenant : tenants) {
                preliminaryResults.add(computeArrears(tenant, when));
            }

            int currentPage = 0;
            int currentPagePosition = 0;
            int totalRows = 0;
            boolean hasMoreRows = false;
            Vector<MockupTenantsArrearsDTO> data = new Vector<MockupTenantsArrearsDTO>();
            while (!preliminaryResults.isEmpty()) {
                MockupTenantsArrearsDTO arrears = preliminaryResults.poll();
                ++currentPagePosition;
                ++totalRows;
                if (currentPagePosition > pageSize) {
                    ++currentPage;
                    currentPagePosition = 1;
                }
                if (currentPage < pageNumber) {
                    continue;
                } else if (currentPage == pageNumber) {
                    data.add(arrears);
                } else {
                    hasMoreRows = true;
                    break;
                }

            }
            totalRows += preliminaryResults.size();

            EntitySearchResult<MockupTenantsArrearsDTO> result = new EntitySearchResult<MockupTenantsArrearsDTO>();
            result.setData(data);
            result.setTotalRows(totalRows);
            result.hasMoreData(hasMoreRows);

            callback.onSuccess(result);

        } catch (Throwable error) {
            callback.onFailure(new Error(error));
        }
    }

    private static MockupTenantsArrearsDTO computeArrears(MockupTenant tenant, LogicalDate when) {
        MockupTenantsArrearsDTO arrearsEntity = tenant.clone(MockupTenantsArrearsDTO.class);

        EntityQueryCriteria<MockupArrear> criteria = new EntityQueryCriteria<MockupArrear>(MockupArrear.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), tenant));
        criteria.add(new PropertyCriterion(criteria.proto().month(), Restriction.LESS_THAN_OR_EQUAL, when));
        criteria.desc(criteria.proto().month().getFieldName());

        List<MockupArrear> arrears = Persistence.service().query(criteria);
        arrearsEntity.arrears1MonthAgo().setValue(arrears.size() > 0 ? arrears.get(0).amount().getValue() : 0d);
        arrearsEntity.arrears2MonthsAgo().setValue(arrears.size() > 1 ? arrears.get(1).amount().getValue() : 0d);
        arrearsEntity.arrears3MonthsAgo().setValue(arrears.size() > 2 ? arrears.get(2).amount().getValue() : 0d);
        arrearsEntity.arrears4MonthsAgo().setValue(arrears.size() > 3 ? arrears.get(3).amount().getValue() : 0d);

        return arrearsEntity;
    }

}