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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentTenantDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;

public class ArrearsGadgetServiceImpl implements ArrearsGadgetService {

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, Vector<Building> buildingsFilter) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        ArrearsGadgetDataDTO data = EntityFactory.create(ArrearsGadgetDataDTO.class);
        calculateArrearsSummary(data.buckets(), buildingsFilter);
        countDelinquentTenants(data.delinquentTenants(), buildingsFilter);

        callback.onSuccess(data);
    }

    @Override
    public void makeTenantCriteria(AsyncCallback<EntityListCriteria<DelinquentTenantDTO>> callback, Vector<Building> buildingsFilter, String criteriaPreset) {
        callback.onSuccess(delinquentTenantsCriteria(EntityListCriteria.create(DelinquentTenantDTO.class), buildingsFilter, criteriaPreset));
    }

    <Criteria extends EntityQueryCriteria<? extends Tenant>> Criteria delinquentTenantsCriteria(Criteria criteria,
            Vector<Building> buildingsFilter, String criteriaPreset) {

//        criteria.isCurrent(criteria.proto().leaseTermV());
        criteria.add(PropertyCriterion.eq(criteria.proto().lease().billingAccount().arrearsSnapshots().$().toDate(), OccupancyFacade.MAX_DATE));

        ArrearsGadgetDataDTO proto = EntityFactory.getEntityPrototype(ArrearsGadgetDataDTO.class);
        IObject<?> member = proto.getMember(new Path(criteriaPreset));

        if (proto.outstandingTotal() == member | proto.delinquentTenants() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().arrearsAmount(),
                    BigDecimal.ZERO));
        } else if (proto.outstandingThisMonth() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().bucketThisMonth(),
                    BigDecimal.ZERO));
        } else if (proto.outstanding1to30Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().bucket30(), BigDecimal.ZERO));
        } else if (proto.outstanding31to60Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().bucket60(), BigDecimal.ZERO));
        } else if (proto.outstanding61to90Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().bucket90(), BigDecimal.ZERO));
        } else if (proto.outstanding91andMoreDays() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets().bucketOver90(),
                    BigDecimal.ZERO));
        }
        return criteria;
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
                add(aggregatedBuckets.bucketThisMonth(), snapshot.totalAgingBuckets().bucketThisMonth());
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

    private void countDelinquentTenants(IPrimitive<Integer> delinquentTenants, Vector<Building> queryParams) {
        delinquentTenants.setValue(Persistence.service().count(//@formatter:off
                delinquentTenantsCriteria(EntityQueryCriteria.create(Tenant.class),
                queryParams,
                EntityFactory.getEntityPrototype(ArrearsGadgetDataDTO.class).delinquentTenants().getPath().toString())
        ));//@formatter:on
    }

    private void add(IPrimitive<BigDecimal> a, IPrimitive<BigDecimal> b) {
        a.setValue(a.getValue().add(b.getValue()));
    }
}
