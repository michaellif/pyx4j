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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsSummary;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsReportServiceImpl implements ArrearsReportService {

    private final EntityDtoBinder<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO> DTO_BINDER = new EntityDtoBinder<LeaseArrearsSnapshot, LeaseArrearsSnapshotDTO>(
            LeaseArrearsSnapshot.class, LeaseArrearsSnapshotDTO.class) {
        @Override
        protected void bind() {

            bind(dtoProto.fromDate(), dboProto.fromDate());
            bind(dtoProto.arrearsAmount(), dboProto.arrearsAmount());
            bind(dtoProto.creditAmount(), dboProto.creditAmount());
            bind(dtoProto.totalBalance(), dboProto.totalBalance());
            bind(dtoProto.lmrToUnitRentDifference(), dboProto.lmrToUnitRentDifference());

            // references
            bind(dtoProto.billingAccount().lease().unit().belongsTo().propertyCode(), dboProto.billingAccount().lease().unit().belongsTo().propertyCode());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().info().name(), dboProto.billingAccount().lease().unit().belongsTo().info().name());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().info().address().streetNumber(), dboProto.billingAccount().lease().unit().belongsTo()
                    .info().address().streetNumber());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().info().address().streetName(), dboProto.billingAccount().lease().unit().belongsTo()
                    .info().address().streetName());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().info().address().province().name(), dboProto.billingAccount().lease().unit().belongsTo()
                    .info().address().province().name());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().info().address().country().name(), dboProto.billingAccount().lease().unit().belongsTo()
                    .info().address().country().name());
            bind(dtoProto.billingAccount().lease().unit().belongsTo().complex().name(), dboProto.billingAccount().lease().unit().belongsTo().complex().name());
            bind(dtoProto.billingAccount().lease().unit().info().number(), dboProto.billingAccount().lease().unit().info().number());
            bind(dtoProto.billingAccount().lease().leaseId(), dboProto.billingAccount().lease().leaseId());
            bind(dtoProto.billingAccount().lease().leaseFrom(), dboProto.billingAccount().lease().leaseFrom());
            bind(dtoProto.billingAccount().lease().leaseTo(), dboProto.billingAccount().lease().leaseTo());

        }
    };

    @Override
    public void arrearsList(AsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>> callback, Vector<Criterion> customCriteria,
            Vector<Building> buildingStubs, LogicalDate asOf, Vector<Sort> sorting, int pageNumber, int pageSize) {

        EntitySearchResult<LeaseArrearsSnapshot> roster = ServerSideFactory.create(ARFacade.class).getArrearsSnapshotRoster(asOf, buildingStubs,
                customCriteria, toDBOSortingCriteria(sorting), pageNumber, pageSize);

        Vector<LeaseArrearsSnapshotDTO> rosterDTO = new Vector<LeaseArrearsSnapshotDTO>();

        for (LeaseArrearsSnapshot snapshot : roster.getData()) {
            rosterDTO.add(toSnapshotDTO(snapshot));
        }

        EntitySearchResult<LeaseArrearsSnapshotDTO> result = new EntitySearchResult<LeaseArrearsSnapshotDTO>();
        result.setData(new Vector<LeaseArrearsSnapshotDTO>(rosterDTO));
        result.setTotalRows(roster.getTotalRows());
        result.hasMoreData(roster.hasMoreData());
        callback.onSuccess(result);
    }

    @Override
    public void summary(AsyncCallback<EntitySearchResult<MockupArrearsSummary>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sorting,
            int pageNumber, int pageSize) {
        // TODO
    }

    @Override
    public void arrearsMonthlyComparison(AsyncCallback<Vector<Vector<Double>>> callback, Vector<Key> buildingPKs, int yearsAgo) {
        // TODO
    }

    private LeaseArrearsSnapshotDTO toSnapshotDTO(LeaseArrearsSnapshot snapshot) {
        Persistence.service().retrieve(snapshot.billingAccount());
        Persistence.service().retrieve(snapshot.billingAccount().lease());
        Persistence.service().retrieve(snapshot.billingAccount().lease().unit());
        Persistence.service().retrieve(snapshot.billingAccount().lease().unit().belongsTo());

        LeaseArrearsSnapshotDTO dto = DTO_BINDER.createDTO(snapshot);

        dto.selectedBuckets().set(snapshot.totalAgingBuckets().duplicate());

        return dto;
    }

    private Vector<Sort> toDBOSortingCriteria(Vector<Sort> dtoSorting) {
        // TODO
        return new Vector<Sort>();
    }

}