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
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrear;

public class ArrearsReportServiceImpl implements ArrearsReportService {
    private static final SortingFactory<MockupArrear> SORTING_FACTORY = new SortingFactory<MockupArrear>(MockupArrear.class);

    @Override
    public void arrearsList(AsyncCallback<EntitySearchResult<MockupArrear>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sortingCriteria,
            int pageNumber, int pageSize) {
        // TODO don't forget to implement sorting
        try {
            EntityQueryCriteria<MockupArrear> criteria = new EntityQueryCriteria<MockupArrear>(MockupArrear.class);
            // adjust the order of results in order to select the most recent statuses
            sortingCriteria.add(0, new Sort(criteria.proto().statusTimestamp().getPath().toString(), true));
            criteria.setSorts(sortingCriteria);
            criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.LESS_THAN_OR_EQUAL, when));

            final List<MockupArrear> allArrears = new ArrayList<MockupArrear>();
            if (!buildingPKs.isEmpty()) {
                // TODO consited make this some other way when it's gonna be possible to make query for set of buildings, i.e. something like: 
                // criteria.add(PropertyCriterion.in(criteria.proto().belongsTo().belongsTo(), buildings));
                for (Key pk : buildingPKs) {
                    PropertyCriterion eqPk = PropertyCriterion.eq(criteria.proto().building(), pk);
                    criteria.add(eqPk);
                    allArrears.addAll(Persistence.service().query(criteria));
                    criteria.getFilters().remove(eqPk);
                }
            } else {
                allArrears.addAll(Persistence.service().query(criteria));
            }

            final int capacity = allArrears.size() + 1;
            final List<MockupArrear> preliminaryResults = new ArrayList<MockupArrear>(capacity);
            final HashSet<Key> alreadyAddedTenants = new HashSet<Key>(capacity);

            // choose only the most recent statuses (we asked the query to sort the results, hence the most recent ones must come first)
            for (MockupArrear arrear : allArrears) {
                if (alreadyAddedTenants.add(arrear.belongsTo().getPrimaryKey())) {
                    preliminaryResults.add(arrear);
                }
            }

            // fix screwed sorting in case of more than one building
            if (!buildingPKs.isEmpty()) {
                SORTING_FACTORY.sortDto(preliminaryResults, sortingCriteria);
            }

            Vector<MockupArrear> data = new Vector<MockupArrear>();
            int totalRows = preliminaryResults.size();
            boolean hasMoreRows = false;

            int currentPage = 0;
            int currentPagePosition = 0;

            for (MockupArrear arrear : preliminaryResults) {
                ++currentPagePosition;
                if (currentPagePosition > pageSize) {
                    ++currentPage;
                    currentPagePosition = 1;
                }
                if (currentPage < pageNumber) {
                    continue;
                } else if (currentPage == pageNumber) {
                    data.add(arrear);
                } else {
                    hasMoreRows = true;
                    break;
                }

            }

            EntitySearchResult<MockupArrear> result = new EntitySearchResult<MockupArrear>();
            result.setData(data);
            result.setTotalRows(totalRows);
            result.hasMoreData(hasMoreRows);
            callback.onSuccess(result);

        } catch (Throwable error) {
            callback.onFailure(new Error(error));
        }
    }

}