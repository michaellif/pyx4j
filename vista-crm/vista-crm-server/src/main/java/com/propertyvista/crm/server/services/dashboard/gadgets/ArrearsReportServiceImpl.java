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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.util.DTOSortingFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrear;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenant;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenantsArrearsDTO;

public class ArrearsReportServiceImpl implements ArrearsReportService {
    private static DTOSortingFactory<MockupTenantsArrearsDTO> SORT_FACTORY = new DTOSortingFactory<MockupTenantsArrearsDTO>(MockupTenantsArrearsDTO.class);

    @Override
    public void arrearsList(AsyncCallback<EntitySearchResult<MockupTenantsArrearsDTO>> callback, Vector<Key> buildings, LogicalDate when,
            Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {
        try {
            // retrieve all the mockup tenants that present or were in the buildings on the specified date
            // then:
            //  for each tenant compute the arrears
            //  sort the results
            // choose the list for the requested page                     
            List<Sort> dboSortingCriteria = DTOSortingFactory.extractSortCriteriaForDboProperties(sortingCriteria, getDbo2DtoMemberMap());
            EntityListCriteria<MockupTenant> criteria = new EntityListCriteria<MockupTenant>(MockupTenant.class);
            criteria.setSorts(dboSortingCriteria);
            HashSet<Key> buildingsSet = new HashSet<Key>(buildings);
            if (!buildings.isEmpty()) {
                // FIXME make the following line work and remove "manual" filtering
//                criteria.add(PropertyCriterion.in(criteria.proto().belongsTo().belongsTo().id(), buildings));
            }

            criteria.add(new PropertyCriterion(criteria.proto().moveIn(), Restriction.LESS_THAN_OR_EQUAL, when));
//            criteria.add(new PropertyCriterion(criteria.proto().moveOut(), Restriction.LESS_THAN_OR_EQUAL, when));
            final List<MockupTenant> tenants = Persistence.service().query(criteria);

            int totalRows = 0;
            boolean hasMoreRows = false;
            Vector<MockupTenantsArrearsDTO> data = new Vector<MockupTenantsArrearsDTO>();
            if (!tenants.isEmpty()) {
                Queue<MockupTenantsArrearsDTO> preliminaryResults;
                if (sortingCriteria.isEmpty()) {
                    preliminaryResults = new ArrayDeque<MockupTenantsArrearsDTO>(tenants.size());
                } else {
                    preliminaryResults = new PriorityQueue<MockupTenantsArrearsDTO>(tenants.size(), SORT_FACTORY.createDtoComparator(sortingCriteria));
                }
                if (!buildingsSet.isEmpty()) {
                    for (MockupTenant tenant : tenants) {
                        MockupTenantsArrearsDTO arrears = computeArrears(tenant, when);
                        if (buildingsSet.contains(arrears.belongsTo().belongsTo().id().getValue())) {
                            preliminaryResults.add(arrears);
                        }
                    }
                } else {
                    for (MockupTenant tenant : tenants) {
                        preliminaryResults.add(computeArrears(tenant, when));
                    }
                }

                int currentPage = 0;
                int currentPagePosition = 0;

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
            }

            EntitySearchResult<MockupTenantsArrearsDTO> result = new EntitySearchResult<MockupTenantsArrearsDTO>();
            result.setData(data);
            result.setTotalRows(totalRows);
            result.hasMoreData(hasMoreRows);

            callback.onSuccess(result);

        } catch (Throwable error) {
            callback.onFailure(new Error(error));
        }
    }

    private static Map<String, String> getDbo2DtoMemberMap() {
        // TODO convert to on demand initialization
        HashMap<String, String> memberMap = new HashMap<String, String>();
        MockupTenant dboProto = EntityFactory.getEntityPrototype(MockupTenant.class);
        MockupTenantsArrearsDTO dtoProto = EntityFactory.getEntityPrototype(MockupTenantsArrearsDTO.class);

        memberMap.put(dtoProto.id().getPath().toString(), dboProto.id().getPath().toString());
        memberMap.put(dtoProto.belongsTo().info().number().getPath().toString(), dboProto.belongsTo().info().number().getPath().toString());
        memberMap.put(dtoProto.belongsTo().belongsTo().getPath().toString(), dboProto.belongsTo().belongsTo().getPath().toString());
        memberMap.put(dtoProto.firstName().getPath().toString(), dboProto.firstName().getPath().toString());
        memberMap.put(dtoProto.lastName().getPath().toString(), dboProto.lastName().getPath().toString());
        return memberMap;
    }

    private static MockupTenantsArrearsDTO computeArrears(MockupTenant tenant, LogicalDate when) {
        MockupTenantsArrearsDTO arrearsEntity = tenant.clone(MockupTenantsArrearsDTO.class);
        Persistence.service().retrieve(arrearsEntity.belongsTo());
        Persistence.service().retrieve(arrearsEntity.belongsTo().belongsTo());
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