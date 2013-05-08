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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsGadgetServiceImpl implements ArrearsGadgetService {

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        ArrearsGadgetDataDTO data = EntityFactory.create(ArrearsGadgetDataDTO.class);

        calculateArrearsSummary(data.buckets(), buildingsFilter);
        count(data.delinquentLeases(), buildingsFilter);
        count(data.outstandingThisMonthCount(), buildingsFilter);
        count(data.outstanding1to30DaysCount(), buildingsFilter);
        count(data.outstanding31to60DaysCount(), buildingsFilter);
        count(data.outstanding61to90DaysCount(), buildingsFilter);
        count(data.outstanding91andMoreDaysCount(), buildingsFilter);

        callback.onSuccess(data);
    }

    @Override
    public void makeDelinquentLeaseCriteria(AsyncCallback<EntityListCriteria<DelinquentLeaseDTO>> callback, Vector<Building> buildingsFilter,
            String criteriaPreset) {
        callback.onSuccess(delinquentLeasesCriteria(buildingsFilter, criteriaPreset));
    }

    private void calculateArrearsSummary(AgingBuckets aggregatedBuckets, Vector<Building> buildings) {

        aggregatedBuckets.bucketThisMonth().setValue(BigDecimal.ZERO);
        aggregatedBuckets.bucketCurrent().setValue(BigDecimal.ZERO);
        aggregatedBuckets.bucket30().setValue(BigDecimal.ZERO);
        aggregatedBuckets.bucket60().setValue(BigDecimal.ZERO);
        aggregatedBuckets.bucket90().setValue(BigDecimal.ZERO);
        aggregatedBuckets.bucketOver90().setValue(BigDecimal.ZERO);
        aggregatedBuckets.arrearsAmount().setValue(BigDecimal.ZERO);
        aggregatedBuckets.totalBalance().setValue(BigDecimal.ZERO);
        aggregatedBuckets.creditAmount().setValue(BigDecimal.ZERO);

        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);

        for (Building b : buildings) {
            BuildingArrearsSnapshot snapshot = arFacade.getArrearsSnapshot(b, Util.dayOfCurrentTransaction());
            if (snapshot == null) {
                continue;
            } else {
                if (!snapshot.totalAgingBuckets().bucketThisMonth().isNull()) {
                    add(aggregatedBuckets.bucketThisMonth(), snapshot.totalAgingBuckets().bucketThisMonth());
                }
                add(aggregatedBuckets.bucketCurrent(), snapshot.totalAgingBuckets().bucketCurrent());
                add(aggregatedBuckets.bucket30(), snapshot.totalAgingBuckets().bucket30());
                add(aggregatedBuckets.bucket60(), snapshot.totalAgingBuckets().bucket60());
                add(aggregatedBuckets.bucket90(), snapshot.totalAgingBuckets().bucket90());
                add(aggregatedBuckets.bucketOver90(), snapshot.totalAgingBuckets().bucketOver90());
                add(aggregatedBuckets.arrearsAmount(), snapshot.totalAgingBuckets().arrearsAmount());
                add(aggregatedBuckets.totalBalance(), snapshot.totalAgingBuckets().totalBalance());
                add(aggregatedBuckets.creditAmount(), snapshot.totalAgingBuckets().creditAmount());
            }
        }
    }

    private void count(IPrimitive<Integer> counter, Vector<Building> buildings) {
        counter.setValue(Persistence.service().count(
                new DelinquentLeaseListServiceImpl().convertCriteria(delinquentLeasesCriteria(buildings, counter.getPath().toString()))));
    }

    private EntityListCriteria<DelinquentLeaseDTO> delinquentLeasesCriteria(Vector<Building> buildingsFilter, String criteriaPreset) {

        EntityListCriteria<DelinquentLeaseDTO> criteria = EntityListCriteria.create(DelinquentLeaseDTO.class);
        if (!buildingsFilter.isEmpty()) {
            criteria.in(criteria.proto().building(), buildingsFilter);
        }

        criteria.add(PropertyCriterion.eq(criteria.proto().asOf(), Util.dayOfCurrentTransaction()));

        ArrearsGadgetDataDTO proto = EntityFactory.getEntityPrototype(ArrearsGadgetDataDTO.class);
        IObject<?> member = proto.getMember(new Path(criteriaPreset));

        if (proto.outstandingTotal() == member | proto.delinquentLeases() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().arrearsAmount(), BigDecimal.ZERO));
        } else if (proto.outstandingThisMonth() == member | proto.outstandingThisMonthCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucketThisMonth(), BigDecimal.ZERO));
        } else if (proto.outstanding1to30Days() == member | proto.outstanding1to30DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket30(), BigDecimal.ZERO));
        } else if (proto.outstanding31to60Days() == member | proto.outstanding31to60DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket60(), BigDecimal.ZERO));
        } else if (proto.outstanding61to90Days() == member | proto.outstanding61to90DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket90(), BigDecimal.ZERO));
        } else if (proto.outstanding91andMoreDays() == member | proto.outstanding91andMoreDaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucketOver90(), BigDecimal.ZERO));
        }

        return criteria;
    }

    private static void add(IPrimitive<BigDecimal> a, IPrimitive<BigDecimal> b) {
        a.setValue(a.getValue().add(b.getValue()));
    }
}
