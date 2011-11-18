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
import java.util.Arrays;
import java.util.HashMap;
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
import com.propertyvista.crm.server.util.SeqUtils;
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsSummary;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class ArrearsReportServiceImpl implements ArrearsReportService {
    private static final SortingFactory<MockupArrearsState> SORTING_FACTORY = new SortingFactory<MockupArrearsState>(MockupArrearsState.class);

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
            when = new LogicalDate(AnalysisResolution.Month.intervalStart(when.getTime()));
            criteria.add(PropertyCriterion.eq(criteria.proto().statusTimestamp(), when));

            if (!buildingPKs.isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().belongsTo().belongsTo(), buildingPKs));
            }
            final List<MockupArrearsState> allArrears = Persistence.service().query(criteria);

            final int capacity = allArrears.size() + 1; // add 1 in to avoid failure if the result set size is 0           
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

    @Override
    public void summary(AsyncCallback<EntitySearchResult<ArrearsSummary>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sorting,
            int pageNumber, int pageSize) {
        try {
            EntityQueryCriteria<ArrearsSummary> criteria = new EntityQueryCriteria<ArrearsSummary>(ArrearsSummary.class);
            if (!buildingPKs.isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().belongsTo(), buildingPKs));
            }
            criteria.setSorts(sorting);
            when = when != null ? when : new LogicalDate();
            when = new LogicalDate(AnalysisResolution.Month.intervalStart(when.getTime()));
            criteria.add(PropertyCriterion.eq(criteria.proto().statusTimestamp(), when));

            List<ArrearsSummary> arrearsForEachBuilding = Persistence.service().query(criteria);

            //@formatter:off
            SeqUtils.Sum<ArrearsSummary> sum = new SeqUtils.Sum<ArrearsSummary>(ArrearsSummary.class,                    
                    Arrays.asList(
                        criteria.proto().thisMonth().getPath(),
                        criteria.proto().monthAgo().getPath(),
                        criteria.proto().twoMonthsAgo().getPath(),
                        criteria.proto().threeMonthsAgo().getPath(),
                        criteria.proto().overFourMonthsAgo().getPath(),
                        criteria.proto().arBalance().getPath())
            );            
            //@formatter:on
            ArrearsSummary summary = SeqUtils.foldl(sum, arrearsForEachBuilding);

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

    @Override
    public void arrearsMonthlyComparison(AsyncCallback<Vector<Vector<Double>>> callback, Vector<Key> buildingPKs, int yearsAgo) {
        try {
            if (yearsAgo < 1) {
                throw new Error("sorry, but you need to provide number of years that at least greater than zero");
            }
            final LogicalDate when = new LogicalDate();
            final LogicalDate lastDay = new LogicalDate(AnalysisResolution.Year.intervalEnd(when.getTime()));
            final LogicalDate firstDay = yearsAgo(yearsAgo, lastDay);

            EntityQueryCriteria<ArrearsSummary> criteria = new EntityQueryCriteria<ArrearsSummary>(ArrearsSummary.class);
            if (!buildingPKs.isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().belongsTo(), buildingPKs));
            }
            //@formatter:off
            List<Sort> sorting = Arrays.asList(
                    new Sort(criteria.proto().statusTimestamp().getPath().toString(), false),
                    new Sort(criteria.proto().belongsTo().getPath().toString(), false));
            //@formatter:on
            criteria.setSorts(sorting);
            criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.GREATER_THAN_OR_EQUAL, firstDay));
            criteria.add(new PropertyCriterion(criteria.proto().statusTimestamp(), Restriction.LESS_THAN, lastDay));

            List<ArrearsSummary> buildingMonthlyArrears = Persistence.service().query(criteria);

            List<ArBalanceHolder> summariesPerEachMonth = computeSummariesPerEachMonth(buildingMonthlyArrears);

            HashMap<Integer, Vector<ArBalanceHolder>> comparison = new HashMap<Integer, Vector<ArBalanceHolder>>();
            for (int i = 0; i < 12; ++i) {
                comparison.put(i, new Vector<ArBalanceHolder>());
            }
            for (ArBalanceHolder monthlyArrears : summariesPerEachMonth) {
                comparison.get(monthlyArrears.timestamp.getMonth()).add(monthlyArrears);
            }

            // prepare the result: if by some accident data for some months is missing, fill in the blanks with NAN values
            // also: we don't have to send the dates to the server because the date marks are implied by the result
            Vector<Vector<Double>> result = new Vector<Vector<Double>>();
            for (Vector<ArBalanceHolder> monthlyComparison : comparison.values()) {
                Vector<Double> monthlyComparisonResult = new Vector<Double>();
                int prevYear = firstDay.getYear();

                for (ArBalanceHolder balanceHolder : monthlyComparison) {
                    int diff = balanceHolder.timestamp.getYear() - prevYear;
                    while (diff-- > 1) {
                        monthlyComparisonResult.add(Double.NaN);
                    }
                    monthlyComparisonResult.add(balanceHolder.balance);
                    prevYear = balanceHolder.timestamp.getYear();
                }
                int diff = lastDay.getYear() - prevYear;
                while (diff-- > 1) {
                    monthlyComparisonResult.add(Double.NaN);
                }
                result.add(monthlyComparisonResult);
            }

            callback.onSuccess(result);
        } catch (Throwable error) {
            callback.onFailure(error);
        }
    }

    @SuppressWarnings("deprecation")
    private static LogicalDate yearsAgo(int yearsAgo, LogicalDate day) {
        final LogicalDate firstDay = new LogicalDate(AnalysisResolution.Year.intervalStart(day.getTime()));
        firstDay.setYear(firstDay.getYear() - yearsAgo);
        return firstDay;
    }

    private static List<ArBalanceHolder> computeSummariesPerEachMonth(List<ArrearsSummary> buildingMonthlyArrears) {
        long previousMonth = -1l;
        List<ArBalanceHolder> summaryPerEachMonth = new ArrayList<ArBalanceHolder>();
        List<ArrearsSummary> buldingArrearsOfTheSameMonth = new ArrayList<ArrearsSummary>();

        for (ArrearsSummary arrears : buildingMonthlyArrears) {
            long thisMonth = arrears.statusTimestamp().getValue().getTime();
            if (thisMonth != previousMonth) {
                if (!buldingArrearsOfTheSameMonth.isEmpty()) {
                    summaryPerEachMonth.add(new ArBalanceHolder(buldingArrearsOfTheSameMonth.get(0).statusTimestamp().getValue(),
                            summarizeArBalance(buldingArrearsOfTheSameMonth)));
                    buldingArrearsOfTheSameMonth = new ArrayList<ArrearsSummary>(buldingArrearsOfTheSameMonth.size());
                }
                previousMonth = thisMonth;
            }
            buldingArrearsOfTheSameMonth.add(arrears);
        }
        if (!buldingArrearsOfTheSameMonth.isEmpty()) {
            summaryPerEachMonth.add(new ArBalanceHolder(buldingArrearsOfTheSameMonth.get(0).statusTimestamp().getValue(),
                    summarizeArBalance(buldingArrearsOfTheSameMonth)));
        }

        return summaryPerEachMonth;
    }

    private static double summarizeArBalance(Iterable<ArrearsSummary> arrearsCollection) {
        double sum = 0.0;
        for (ArrearsSummary arrears : arrearsCollection) {
            sum += arrears.arBalance().getValue();
        }
        return sum;
    }

    private static class ArBalanceHolder {
        public final LogicalDate timestamp;

        public final double balance;

        public ArBalanceHolder(LogicalDate timestamp, double balance) {
            this.timestamp = timestamp;
            this.balance = balance;
        }
    }
}