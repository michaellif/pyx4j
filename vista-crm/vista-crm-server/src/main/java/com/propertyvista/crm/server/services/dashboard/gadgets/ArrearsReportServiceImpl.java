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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsSummary;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;

public class ArrearsReportServiceImpl implements ArrearsReportService {
    private static final SortingFactory<MockupArrearsState> SORTING_FACTORY = new SortingFactory<MockupArrearsState>(MockupArrearsState.class);

    @SuppressWarnings("deprecation")
    @Override
    public void arrearsList(AsyncCallback<EntitySearchResult<MockupArrearsState>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sorting,
            int pageNumber, int pageSize) {
        try {
            EntityQueryCriteria<MockupArrearsState> criteria = new EntityQueryCriteria<MockupArrearsState>(MockupArrearsState.class);
            // adjust the order of results in order to select the most recent statuses
            ArrayList<Sort> sortingCriteria = new ArrayList<Sort>();
            sortingCriteria.add(new Sort(criteria.proto().belongsTo().getPath().toString(), false));
            sortingCriteria.add(new Sort(criteria.proto().statusTimestamp().getPath().toString(), true));
            sortingCriteria.addAll(sorting);
            criteria.setSorts(sortingCriteria);

            when = when != null ? when : new LogicalDate();
            LogicalDate monthAgo = new LogicalDate(when);
            if (when.getMonth() != 0) {
                monthAgo.setMonth((11 + when.getMonth() - 1) % 11);
            } else {
                monthAgo.setMonth(1);
                monthAgo.setYear(when.getYear() - 1);
            }
            // TODO this is just for demo, IRL it shouldn't look the same at all...
            criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.GREATER_THAN_OR_EQUAL, monthAgo));
            criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.LESS_THAN_OR_EQUAL, when));

            final List<MockupArrearsState> allArrears = new ArrayList<MockupArrearsState>();
            if (!buildingPKs.isEmpty()) {
                // TODO make this some other way when it's gonna be possible to make query for set of buildings, i.e. something like: 
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
            final List<MockupArrearsState> preliminaryResults = new ArrayList<MockupArrearsState>(capacity);
            // choose only the most recent statuses (we asked the query to sort the results, hence the most recent ones must come first)
            Key previousTenantPk = null;

            for (MockupArrearsState arrear : allArrears) {
                Key thisTenantPk = arrear.belongsTo().getPrimaryKey();
                if (!thisTenantPk.equals(previousTenantPk)) {
                    preliminaryResults.add(arrear);
                    previousTenantPk = thisTenantPk;
                }
            }

            // TODO make this for for all properties 
            SORTING_FACTORY.sortDto(preliminaryResults, sorting);

            Vector<MockupArrearsState> pageData = new Vector<MockupArrearsState>();
            int totalRows = preliminaryResults.size();
            boolean hasMoreRows = false;

            int currentPage = 0;
            int currentPagePosition = 0;

            // FIXME fix bug with wrong page/totalrows/pagenumber mechanism
            for (MockupArrearsState arrear : preliminaryResults) {
                ++currentPagePosition;
                if (currentPagePosition > pageSize) {
                    ++currentPage;
                    currentPagePosition = 1;
                }
                if (currentPage < pageNumber) {
                    continue;
                } else if (currentPage == pageNumber) {
                    pageData.add(arrear);
                } else {
                    hasMoreRows = true;
                    break;
                }

            }

            EntitySearchResult<MockupArrearsState> result = new EntitySearchResult<MockupArrearsState>();
            result.setData(pageData);
            result.setTotalRows(totalRows);
            result.hasMoreData(hasMoreRows);
            callback.onSuccess(result);

        } catch (Throwable error) {
            callback.onFailure(new Error(error));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void summary(AsyncCallback<EntitySearchResult<ArrearsSummary>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sorting,
            int pageNumber, int pageSize) {
        try {

            List<ArrearsSummary> summaryArrears = new ArrayList<ArrearsSummary>();
            Iterator<Key> buildings = buildingPKs.iterator();

            while (buildingPKs.isEmpty() | buildings.hasNext()) {
                EntityQueryCriteria<ArrearsSummary> criteria = new EntityQueryCriteria<ArrearsSummary>(ArrearsSummary.class);
                if (!buildingPKs.isEmpty()) {
                    criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), buildings.next()));
                }
                List<Sort> sortingCriteria = new ArrayList<Sort>();
                sortingCriteria.add(new Sort(criteria.proto().statusTimestamp().getPath().toString(), true));
                sortingCriteria.addAll(sorting);
                criteria.setSorts(sortingCriteria);

                when = when != null ? when : new LogicalDate();
                LogicalDate monthAgo = new LogicalDate(when);
                if (when.getMonth() != 0) {
                    monthAgo.setMonth((11 + when.getMonth() - 1) % 11);
                } else {
                    monthAgo.setMonth(1);
                    monthAgo.setYear(when.getYear() - 1);
                }
                // TODO this is just for demo, IRL it shouldn't look the same at all...
                criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.GREATER_THAN_OR_EQUAL, monthAgo));
                criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.LESS_THAN_OR_EQUAL, when));
                summaryArrears.addAll(Persistence.service().query(criteria));
                if (buildingPKs.isEmpty()) {
                    break;
                }
            }
            Key pervBuildingKey = null;
            ArrearsSummary summary = EntityFactory.create(ArrearsSummary.class);
            summary.thisMonth().setValue(0.0);
            summary.monthAgo().setValue(0.0);
            summary.twoMonthsAgo().setValue(0.0);
            summary.threeMonthsAgo().setValue(0.0);
            summary.overFourMonthsAgo().setValue(0.0);
            summary.totalBalance().setValue(0.0);
            summary.arBalance().setValue(0.0);

            for (ArrearsSummary arrears : summaryArrears) {
                Key thisBuildingKey = arrears.belongsTo().getPrimaryKey();
                if (!thisBuildingKey.equals(pervBuildingKey)) {
                    pervBuildingKey = thisBuildingKey;
                    summary.thisMonth().setValue(summary.thisMonth().getValue() + arrears.thisMonth().getValue());
                    summary.monthAgo().setValue(summary.monthAgo().getValue() + arrears.monthAgo().getValue());
                    summary.twoMonthsAgo().setValue(summary.twoMonthsAgo().getValue() + arrears.twoMonthsAgo().getValue());
                    summary.threeMonthsAgo().setValue(summary.threeMonthsAgo().getValue() + arrears.threeMonthsAgo().getValue());
                    summary.overFourMonthsAgo().setValue(summary.overFourMonthsAgo().getValue() + arrears.overFourMonthsAgo().getValue());
                    summary.totalBalance().setValue(summary.totalBalance().getValue() + arrears.totalBalance().getValue());
                    summary.arBalance().setValue(summary.arBalance().getValue() + arrears.arBalance().getValue());
                }
            }

            EntitySearchResult<ArrearsSummary> result = new EntitySearchResult<ArrearsSummary>();
            result.setData(new Vector<ArrearsSummary>(1));
            result.getData().add(summary);
            result.setTotalRows(1);
            result.hasMoreData(false);

            callback.onSuccess(result);

        } catch (Throwable error) {
            callback.onFailure(error);
        }
    }
}