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

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetQueryDataDTO;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsGadgetServiceImpl implements ArrearsGadgetService {

    private static final Logger log = LoggerFactory.getLogger(ArrearsGadgetServiceImpl.class);

    @Override
    public void countData(AsyncCallback<ArrearsGadgetDataDTO> callback, ArrearsGadgetQueryDataDTO query) {
        ArrearsGadgetDataDTO data = EntityFactory.create(ArrearsGadgetDataDTO.class);

        calculateArrearsSummary(data.buckets(), query);

        count(data.delinquentLeases(), query);
        count(data.outstandingThisMonthCount(), query);
        count(data.outstanding1to30DaysCount(), query);
        count(data.outstanding31to60DaysCount(), query);
        count(data.outstanding61to90DaysCount(), query);
        count(data.outstanding91andMoreDaysCount(), query);

        callback.onSuccess(data);
    }

    @Override
    public void makeDelinquentLeaseCriteria(AsyncCallback<EntityListCriteria<DelinquentLeaseDTO>> callback, ArrearsGadgetQueryDataDTO query,
            String criteriaPreset) {
        callback.onSuccess(delinquentLeasesCriteria(query, criteriaPreset));
    }

    private void calculateArrearsSummary(LeaseAgingBuckets aggregatedBuckets, ArrearsGadgetQueryDataDTO query) {

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

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        if (!query.buildingsFilter().isEmpty()) {
            criteria.in(criteria.proto().id(), new Vector<Building>(query.buildingsFilter()));
        }
        ICursorIterator<Building> i = Persistence.secureQuery(null, criteria, AttachLevel.IdOnly);
        while (i.hasNext()) {
            Building b = i.next();
            BuildingArrearsSnapshot snapshot = arFacade.getArrearsSnapshot(b, query.asOf().getValue(), true);
            if (snapshot == null) {
                continue;
            } else {
                AgingBuckets<?> buckets = null;

                foundBuckets: for (AgingBuckets<?> bucketsCandidate : snapshot.agingBuckets()) {
                    if (bucketsCandidate.arCode().getValue() == query.category().getValue()) {
                        buckets = bucketsCandidate;
                        break foundBuckets;
                    }
                }
                if (buckets == null) {
                    continue;
                }

                if (!buckets.bucketThisMonth().isNull()) {
                    add(aggregatedBuckets.bucketThisMonth(), buckets.bucketThisMonth());
                }
                add(aggregatedBuckets.bucketCurrent(), buckets.bucketCurrent());
                add(aggregatedBuckets.bucket30(), buckets.bucket30());
                add(aggregatedBuckets.bucket60(), buckets.bucket60());
                add(aggregatedBuckets.bucket90(), buckets.bucket90());
                add(aggregatedBuckets.bucketOver90(), buckets.bucketOver90());
                add(aggregatedBuckets.arrearsAmount(), buckets.arrearsAmount());
                add(aggregatedBuckets.totalBalance(), buckets.totalBalance());
                add(aggregatedBuckets.creditAmount(), buckets.creditAmount());
            }
        }
        try {
            ((Closeable) i).close();
        } catch (IOException e) {
            log.warn("Warning", e);
        }
    }

    private void count(IPrimitive<Integer> counter, ArrearsGadgetQueryDataDTO query) {
        counter.setValue(Persistence.service().count(
                new DelinquentLeaseListServiceImpl().convertCriteria(delinquentLeasesCriteria(query, counter.getPath().toString()))));
    }

    private EntityListCriteria<DelinquentLeaseDTO> delinquentLeasesCriteria(ArrearsGadgetQueryDataDTO query, String criteriaPreset) {

        EntityListCriteria<DelinquentLeaseDTO> criteria = EntityListCriteria.create(DelinquentLeaseDTO.class);
        if (!query.buildingsFilter().isEmpty()) {
            criteria.in(criteria.proto().building(), query.buildingsFilter());
        }
        criteria.eq(criteria.proto().arrears().arCode(), query.category().getValue());
        criteria.eq(criteria.proto().asOf(), query.asOf().getValue());

        ArrearsGadgetDataDTO proto = EntityFactory.getEntityPrototype(ArrearsGadgetDataDTO.class);
        IObject<?> member = proto.getMember(new Path(criteriaPreset));

        if (proto.buckets().totalBalance() == member | proto.delinquentLeases() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().arrearsAmount(), BigDecimal.ZERO));
        } else if (proto.buckets().bucketThisMonth() == member | proto.outstandingThisMonthCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucketThisMonth(), BigDecimal.ZERO));
        } else if (proto.buckets().bucket30() == member | proto.outstanding1to30DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket30(), BigDecimal.ZERO));
        } else if (proto.buckets().bucket60() == member | proto.outstanding31to60DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket60(), BigDecimal.ZERO));
        } else if (proto.buckets().bucket90() == member | proto.outstanding61to90DaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucket90(), BigDecimal.ZERO));
        } else if (proto.buckets().bucketOver90() == member | proto.outstanding91andMoreDaysCount() == member) {
            criteria.add(PropertyCriterion.gt(criteria.proto().arrears().bucketOver90(), BigDecimal.ZERO));
        }

        return criteria;
    }

    private static void add(IPrimitive<BigDecimal> a, IPrimitive<BigDecimal> b) {
        a.setValue(a.getValue().add(b.getValue()));
    }

}
