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

import com.pyx4j.commons.LogicalDate;
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
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TenantDTO;

public class ArrearsGadgetServiceImpl implements ArrearsGadgetService {

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, Vector<Building> queryParams) {
        ARFacade facade = ServerSideFactory.create(ARFacade.class);

        Vector<Building> buildings = queryParams.isEmpty() ? Persistence.secureQuery(EntityQueryCriteria.create(Building.class)) : queryParams;

        AgingBuckets proto = EntityFactory.create(AgingBuckets.class);

        @SuppressWarnings("unchecked")
        IPrimitive<BigDecimal>[] propertiesToAggregate = new IPrimitive[] {//@formatter:off
                proto.bucketCurrent(),
                proto.bucket30(),
                proto.bucket60(),
                proto.bucket90(),
                proto.bucketOver90(),
                proto.totalBalance(),
                proto.arrearsAmount(),
                proto.creditAmount()
        };//@formatter:on

        AgingBuckets totalBuckets = EntityFactory.create(AgingBuckets.class);
        LogicalDate asOf = Utils.dayOfCurrentTransaction();

        for (IPrimitive<BigDecimal> property : propertiesToAggregate) {
            totalBuckets.setValue(property.getPath(), new BigDecimal("0.0"));
        }

        for (Building b : buildings) {

            BuildingArrearsSnapshot snapshot = facade.getArrearsSnapshot(b, asOf);
            if (snapshot == null) {
                continue;
            }
            AgingBuckets snapshotBuckets = snapshot.totalAgingBuckets().detach();

            for (IPrimitive<BigDecimal> property : propertiesToAggregate) {
                BigDecimal value = (BigDecimal) totalBuckets.getValue(property.getPath());
                value = value.add((BigDecimal) snapshotBuckets.getValue(property.getPath()));
                totalBuckets.setValue(property.getPath(), value);
            }
        }

        ArrearsGadgetDataDTO data = EntityFactory.create(ArrearsGadgetDataDTO.class);
        data.delinquentTenants().setValue(9001);

        data.bucketThisMonth().setValue(new BigDecimal("5.99"));
        data.buckets().set(totalBuckets);

        callback.onSuccess(data);

    }

    @Override
    public void makeTenantCriteria(AsyncCallback<EntityListCriteria<TenantDTO>> callback, Vector<Building> buildingsFilter, String criteriaPreset) {
        EntityListCriteria<TenantDTO> criteria = EntityListCriteria.create(TenantDTO.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().toDate(),
                OccupancyFacade.MAX_DATE));
        ArrearsGadgetDataDTO proto = EntityFactory.getEntityPrototype(ArrearsGadgetDataDTO.class);
        IObject<?> member = proto.getMember(new Path(criteriaPreset));

        if (proto.outstandingTotal() == member | proto.delinquentTenants() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets()
                    .arrearsAmount(), BigDecimal.ZERO));
        } else if (proto.outstandingThisMonth() == member) {

        } else if (proto.outstanding1to30Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets()
                    .bucket30(), BigDecimal.ZERO));
        } else if (proto.outstanding31to60Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets()
                    .bucket60(), BigDecimal.ZERO));
        } else if (proto.outstanding61to90Days() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets()
                    .bucket90(), BigDecimal.ZERO));
        } else if (proto.outstanding91andMoreDays() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().leaseTermV().holder().lease().billingAccount().arrearsSnapshots().$().totalAgingBuckets()
                    .bucketOver90(), BigDecimal.ZERO));
        }
        callback.onSuccess(criteria);
    }

}
