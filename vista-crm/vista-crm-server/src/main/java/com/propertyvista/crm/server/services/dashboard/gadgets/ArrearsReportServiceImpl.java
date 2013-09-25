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

import static com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter.makeMapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter.PropertyMapper;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsComparisonDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsValueDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsYOYComparisonDataDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsReportServiceImpl implements ArrearsReportService {

    private final static I18n i18n = I18n.get(ArrearsReportServiceImpl.class);

    // reminder: resist the urge to make it static because maybe it's not thread safe
    private final EntityBinder<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO> dtoBinder = new EntityBinder<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO>(
            LeaseArrearsSnapshot.class, LeaseArrearsSnapshotDTO.class) {

        @Override
        protected void bind() {
            bind(toProto.fromDate(), boProto.fromDate());
            bind(toProto.lmrToUnitRentDifference(), boProto.lmrToUnitRentDifference());

            // references
            bind(toProto.billingAccount().lease().unit().building().propertyCode(), boProto.billingAccount().lease().unit().building().propertyCode());
            bind(toProto.billingAccount().lease().unit().building().info().name(), boProto.billingAccount().lease().unit().building().info().name());
            bind(toProto.billingAccount().lease().unit().building().info().address().streetNumber(), boProto.billingAccount().lease().unit().building()
                    .info().address().streetNumber());
            bind(toProto.billingAccount().lease().unit().building().info().address().streetName(), boProto.billingAccount().lease().unit().building().info()
                    .address().streetName());
            bind(toProto.billingAccount().lease().unit().building().info().address().province().name(), boProto.billingAccount().lease().unit().building()
                    .info().address().province().name());
            bind(toProto.billingAccount().lease().unit().building().info().address().country().name(), boProto.billingAccount().lease().unit().building()
                    .info().address().country().name());
            bind(toProto.billingAccount().lease().unit().building().complex().name(), boProto.billingAccount().lease().unit().building().complex().name());
            bind(toProto.billingAccount().lease().unit().info().number(), boProto.billingAccount().lease().unit().info().number());
            bind(toProto.billingAccount().lease().leaseId(), boProto.billingAccount().lease().leaseId());
            bind(toProto.billingAccount().lease().leaseFrom(), boProto.billingAccount().lease().leaseFrom());
            bind(toProto.billingAccount().lease().leaseTo(), boProto.billingAccount().lease().leaseTo());

        }
    };

    @Override
    public void leaseArrearsRoster(AsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>> callback, Vector<Building> buildingsFilter, LogicalDate asOf,
            ARCode.Type arrearsCategory, Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {

        EntityDto2DboCriteriaConverter<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO> criteriaConverter = createCriteriaConverter(arrearsCategory);
        Collection<Criterion> customCriteria = new Vector<Criterion>(); // reserved for future use

        EntitySearchResult<LeaseArrearsSnapshot> roster = ServerSideFactory.create(ARFacade.class).getArrearsSnapshotRoster(//@formatter:off
                asOf,
                buildingsFilter,
                new Vector<Criterion>(criteriaConverter.convertDTOSearchCriteria(customCriteria)),
                new Vector<Sort>(criteriaConverter.convertDTOSortingCriteria(sortingCriteria)),
                pageNumber,
                pageSize);//@formatter:on

        // convert the roster to DTO
        Vector<LeaseArrearsSnapshotDTO> rosterDTO = new Vector<LeaseArrearsSnapshotDTO>();
        for (LeaseArrearsSnapshot snapshot : roster.getData()) {
            rosterDTO.add(toSnapshotDTO(arrearsCategory, snapshot));
        }

        // prepare the result 
        EntitySearchResult<LeaseArrearsSnapshotDTO> result = new EntitySearchResult<LeaseArrearsSnapshotDTO>();
        result.setData(new Vector<LeaseArrearsSnapshotDTO>(rosterDTO));
        result.setTotalRows(roster.getTotalRows());
        result.hasMoreData(roster.hasMoreData());

        callback.onSuccess(result);
    }

    @Override
    public void arrearsMonthlyComparison(AsyncCallback<ArrearsYOYComparisonDataDTO> callback, Vector<Building> buildingsFilter, int yearsAgo) {
        if (yearsAgo < 0 | yearsAgo > YOY_ANALYSIS_CHART_MAX_YEARS_AGO) {
            throw new UserRuntimeException(i18n.tr("the value of years for comparison has to be between 0 and {0}", YOY_ANALYSIS_CHART_MAX_YEARS_AGO));
        }
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        if (!buildingsFilter.isEmpty()) {
            criteria.in(criteria.proto().id(), buildingsFilter);
        }
        Vector<Building> buildings = Persistence.secureQuery(criteria);

        final LogicalDate now = new LogicalDate(SystemDateManager.getDate());

        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(now);

        final ArrearsYOYComparisonDataDTO comparisonData = EntityFactory.create(ArrearsYOYComparisonDataDTO.class);

        final int thisYear = cal.get(Calendar.YEAR);
        final int thisMonth = cal.get(Calendar.MONTH);
        final int thisDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        final int firstMonth = cal.getActualMinimum(Calendar.MONTH);
        final int lastMonth = cal.getActualMaximum(Calendar.MONTH);
        final int lastYear = cal.get(Calendar.YEAR);
        final int firstYear = lastYear - yearsAgo;

        // we assume Gregorian calendar with constant number of 12 month (Hebrew calendar with its pregnant year will NOT work here)
        for (int month = firstMonth; month <= lastMonth; ++month) {
            ArrearsComparisonDTO yoyComparison = comparisonData.comparisonsByMonth().$();
            yoyComparison.month().setValue(month);

            for (int year = firstYear; year <= lastYear; ++year) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                if (!(year == thisYear & month == thisMonth)) {
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else {
                    cal.set(Calendar.DAY_OF_MONTH, thisDayOfMonth);
                }

                ArrearsValueDTO arrearsValue = yoyComparison.values().$();
                arrearsValue.year().setValue(year);
                arrearsValue.totalArrears().setValue(totalArrears(buildings, new LogicalDate(cal.getTime())));

                yoyComparison.values().add(arrearsValue);

            }
            comparisonData.comparisonsByMonth().add(yoyComparison);

        }

        callback.onSuccess(comparisonData);
    }

    /**
     * @return the sum of total arrears of the chosen buildings on <b>asOf</b> date, if <b>asOf</b> is in the future return $0.0,
     */
    private BigDecimal totalArrears(Vector<Building> buildings, LogicalDate asOf) {

        BigDecimal totalArrears = new BigDecimal("0.00");

        LogicalDate today = new LogicalDate(SystemDateManager.getDate());

        if (!asOf.after(today)) { // if we asked for the future value of total arrears return 0            

            ARFacade facade = ServerSideFactory.create(ARFacade.class);

            for (Building b : buildings) {
                ArrearsSnapshot<?> snapshot = facade.getArrearsSnapshot(b, asOf, true);
                if (snapshot != null) {
                    for (AgingBuckets<?> buckets : snapshot.agingBuckets()) {
                        if (buckets.arCode().isNull()) {
                            totalArrears = totalArrears.add(buckets.totalBalance().getValue());
                            break;
                        }
                    }
                }
            }
        }

        return totalArrears;
    }

    private LeaseArrearsSnapshotDTO toSnapshotDTO(ARCode.Type arrearsCategory, LeaseArrearsSnapshot snapshot) {
        Persistence.service().retrieve(snapshot.billingAccount());
        Persistence.service().retrieve(snapshot.billingAccount().lease());
        Persistence.service().retrieve(snapshot.billingAccount().lease().unit());
        Persistence.service().retrieve(snapshot.billingAccount().lease().unit().building());

        LeaseArrearsSnapshotDTO snapshotDTO = dtoBinder.createTO(snapshot);

        AgingBuckets selectedBuckets = null;
        for (AgingBuckets buckets : snapshot.agingBuckets()) {
            if (buckets.arCode().getValue() == arrearsCategory) {
                selectedBuckets = buckets.duplicate();
            }
        }
        if (selectedBuckets == null) {
            selectedBuckets = EntityFactory.create(AgingBuckets.class);
        }
        snapshotDTO.selectedBuckets().set(selectedBuckets);

        return snapshotDTO;
    }

    private EntityDto2DboCriteriaConverter<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO> createCriteriaConverter(final ARCode.Type arrearsCategory) {
        final LeaseArrearsSnapshotDTO dtoProto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshotDTO.class);
        final LeaseArrearsSnapshot dboProto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshot.class);

        PropertyMapper bucketMapper = new PropertyMapper() {
            @Override
            public Path getDboMemberPath(Path dtoMemberPath) {
                if (dtoMemberPath.toString().startsWith(dtoProto.selectedBuckets().getPath().toString())) {
                    return new Path(dtoMemberPath.toString().replace(dtoProto.selectedBuckets().getPath().toString(),
                            dboProto.agingBuckets().$().getPath().toString()));
                } else {
                    return null;
                }
            }

            @Override
            public Serializable convertValue(Serializable value) {
                return value;
            }
        };

        return new EntityDto2DboCriteriaConverter<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO>(LeaseArrearsSnapshot.class, LeaseArrearsSnapshotDTO.class,
                makeMapper(dtoBinder), bucketMapper);
    }

}