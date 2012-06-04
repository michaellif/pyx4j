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

import static com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter.makeMapper;

import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.PmcPaymentTypeInfo;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO.PaymentFeeMeasure;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentReportServiceImpl implements PaymentReportService {

    private final EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO> dtoBinder;

    private final EntityDto2DboCriteriaConverter<PaymentRecord, PaymentRecordForReportDTO> dto2dboCriteriaConverter;

    public PaymentReportServiceImpl() {

        dtoBinder = new EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO>(PaymentRecord.class, PaymentRecordForReportDTO.class) {
            @Override
            protected void bind() {
                bind(dtoProto.billingAccount().accountNumber(), dboProto.billingAccount().accountNumber());
                bind(dtoProto.billingAccount().lease().unit().belongsTo().propertyCode(), dboProto.billingAccount().lease().unit().belongsTo().propertyCode());
                bind(dtoProto.billingAccount().lease().leaseId(), dboProto.billingAccount().lease().leaseId());
                bind(dtoProto.paymentMethod().type(), dboProto.paymentMethod().type());
                bind(dtoProto.paymentMethod().leaseParticipant(), dboProto.paymentMethod().leaseParticipant());
                bind(dtoProto.paymentStatus(), dboProto.paymentStatus());
                bind(dtoProto.createdDate(), dboProto.createdDate());
                bind(dtoProto.receivedDate(), dboProto.receivedDate());
                bind(dtoProto.finalizeDate(), dboProto.finalizeDate());
                bind(dtoProto.targetDate(), dboProto.targetDate());
                bind(dtoProto.amount(), dboProto.amount());
            }
        };

        dto2dboCriteriaConverter = new EntityDto2DboCriteriaConverter<PaymentRecord, PaymentRecordForReportDTO>(PaymentRecord.class,
                PaymentRecordForReportDTO.class, makeMapper(dtoBinder));
    }

    @Override
    public void paymentRecords(AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>> callback, Vector<Building> buildings, LogicalDate targetDate,
            Vector<PaymentType> paymentTypeCriteria, Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize,
            Vector<Sort> sortingCriteria) {

        EntityListCriteria<PaymentRecord> criteria = EntityListCriteria.create(PaymentRecord.class);

        criteria.setPageSize(pageSize);
        criteria.setPageNumber(pageNumber);

        criteria.setSorts(dto2dboCriteriaConverter.convertDTOSortingCriteria(sortingCriteria));

        // set up search criteria
        criteria.add(PropertyCriterion.eq(criteria.proto().lastStatusChangeDate(), targetDate));
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().belongsTo(), buildings));
        }
        if (!paymentTypeCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentMethod().type(), paymentTypeCriteria));
        }
        if (!paymentStatusCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), paymentStatusCriteria));
        }

        // query and return the result
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

    @Override
    public void paymentsSummary(AsyncCallback<EntitySearchResult<PaymentsSummary>> callback, Vector<Building> buildings, LogicalDate targetDate,
            Vector<PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize, Vector<Sort> sortingCriteria) {
        // TODO implement this
        EntitySearchResult<PaymentsSummary> result = new EntitySearchResult<PaymentsSummary>();
        result.setTotalRows(0);
        result.setData(new Vector<PaymentsSummary>());
        result.hasMoreData(false);

        callback.onSuccess(result);
    }

    @Override
    public void paymentsFees(AsyncCallback<Vector<PaymentFeesDTO>> callback) {
        // TODO: WARNING getCurrentPmc() uses current namespace to get currentPmc:        
        final Pmc currentPmc = VistaDeployment.getCurrentPmc();

        PmcPaymentTypeInfo paymentTypeInfo = TaskRunner.runInAdminNamespace(new Callable<PmcPaymentTypeInfo>() {
            @Override
            public PmcPaymentTypeInfo call() throws Exception {
                EntityQueryCriteria<PmcPaymentTypeInfo> criteria = EntityQueryCriteria.create(PmcPaymentTypeInfo.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), currentPmc));
                return Persistence.service().retrieve(criteria);
            }
        });

        Vector<PaymentFeesDTO> paymentFees = new Vector<PaymentFeesDTO>();
        paymentFees.add(PaymentFeesHelper.extractFees(paymentTypeInfo, PaymentFeeMeasure.absolute));
        paymentFees.add(PaymentFeesHelper.extractFees(paymentTypeInfo, PaymentFeeMeasure.relative));

        callback.onSuccess(paymentFees);

    }

    private PaymentRecordForReportDTO makeDTO(PaymentRecord paymentRecordDBO) {

        Persistence.service().retrieve(paymentRecordDBO.billingAccount());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit().belongsTo());
        Persistence.service().retrieve(paymentRecordDBO.paymentMethod().leaseParticipant());

        PaymentRecordForReportDTO paymentRecordDTO = dtoBinder.createDTO(paymentRecordDBO);

        return paymentRecordDTO;
    }

}
