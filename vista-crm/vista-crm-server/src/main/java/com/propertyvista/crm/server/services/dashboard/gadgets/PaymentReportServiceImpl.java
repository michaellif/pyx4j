/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import static com.propertyvista.crm.server.util.EntityDTOHelper.mapper;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.crm.server.util.EntityDTOHelper;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant.Role;

public class PaymentReportServiceImpl implements PaymentReportService {

    private final EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO> dtoBinder;

    private final EntityDTOHelper<PaymentRecord, PaymentRecordForReportDTO> dtoHelper;

    public PaymentReportServiceImpl() {

        dtoBinder = new EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO>(PaymentRecord.class, PaymentRecordForReportDTO.class) {
            @Override
            protected void bind() {
                bind(dtoProto.billingAccount().accountNumber(), dboProto.billingAccount().accountNumber());
                bind(dtoProto.billingAccount().lease().unit().belongsTo().propertyCode(), dboProto.billingAccount().lease().unit().belongsTo().propertyCode());
                bind(dtoProto.billingAccount().lease().leaseId(), dboProto.billingAccount().lease().leaseId());
                bind(dtoProto.paymentMethod().type(), dboProto.paymentMethod().type());
                bind(dtoProto.paymentStatus(), dboProto.paymentStatus());
                bind(dtoProto.createdDate(), dboProto.createdDate());
                bind(dtoProto.receivedDate(), dboProto.receivedDate());
                bind(dtoProto.finalizeDate(), dboProto.finalizeDate());
                bind(dtoProto.targetDate(), dboProto.targetDate());
                bind(dtoProto.amount(), dboProto.amount());
            }
        };

        dtoHelper = new EntityDTOHelper<PaymentRecord, PaymentRecordForReportDTO>(PaymentRecord.class, PaymentRecordForReportDTO.class, mapper(dtoBinder));
    }

    @Override
    public void paymentRecords(AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>> callback, Vector<Building> buildings, LogicalDate targetDate,
            PaymentType paymentTypeCriteria, Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize,
            Vector<Sort> sortingCriteria) {

        EntityListCriteria<PaymentRecord> criteria = EntityListCriteria.create(PaymentRecord.class);

        criteria.setPageSize(pageSize);
        criteria.setPageNumber(pageNumber);

        criteria.setSorts(dtoHelper.convertDTOSortingCriteria(sortingCriteria));

        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().belongsTo(), buildings));
        }
        criteria.add(PropertyCriterion.ge(criteria.proto().targetDate(), targetDate));
        if (paymentTypeCriteria != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentTypeCriteria));
        }
        if (paymentStatusCriteria != null & !paymentStatusCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), paymentStatusCriteria));
        }

        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, criteria, AttachLevel.Attached);
        Vector<PaymentRecordForReportDTO> paymentRecordsPageData = new Vector<PaymentRecordForReportDTO>();
        while (i.hasNext()) {
            paymentRecordsPageData.add(makeDTO(i.next()));
        }

        EntitySearchResult<PaymentRecordForReportDTO> result = new EntitySearchResult<PaymentRecordForReportDTO>();
        result.setTotalRows(Persistence.service().count(criteria));
        result.setData(paymentRecordsPageData);
        result.hasMoreData(result.getTotalRows() > (pageNumber * pageSize + paymentRecordsPageData.size())); // MIN(pageNumber) = 0, so the formula is correct 

        callback.onSuccess(result);
    }

    private PaymentRecordForReportDTO makeDTO(PaymentRecord paymentRecordDBO) {
        Persistence.service().retrieve(paymentRecordDBO.billingAccount());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().version().tenants());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit().belongsTo());

        PaymentRecordForReportDTO paymentRecordDTO = dtoBinder.createDTO(paymentRecordDBO);

        gotPrimaryTenant: for (Tenant tenant : paymentRecordDBO.billingAccount().lease().version().tenants()) {
            if (tenant.role().getValue() == Role.Applicant) {
                paymentRecordDTO.primaryTenant().set(tenant.detach());
                break gotPrimaryTenant;
            }
        }

        return paymentRecordDTO;
    }
}
