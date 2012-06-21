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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
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
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.util.PaymentsSummaryHelper;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentReportServiceImpl implements PaymentReportService {

    private final EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO> dtoBinder;

    private final EntityDto2DboCriteriaConverter<PaymentRecord, PaymentRecordForReportDTO> dto2dboCriteriaConverter;

    public PaymentReportServiceImpl() {

        dtoBinder = new EntityDtoBinder<PaymentRecord, PaymentRecordForReportDTO>(PaymentRecord.class, PaymentRecordForReportDTO.class) {
            @Override
            protected void bind() {
                bind(dtoProto.merchantAccount().accountNumber(), dboProto.merchantAccount().accountNumber());
                bind(dtoProto.billingAccount().lease().unit().building().propertyCode(), dboProto.billingAccount().lease().unit().building().propertyCode());
                bind(dtoProto.billingAccount().lease().leaseId(), dboProto.billingAccount().lease().leaseId());
                bind(dtoProto.paymentMethod().type(), dboProto.paymentMethod().type());
                bind(dtoProto.paymentMethod().customer(), dboProto.paymentMethod().customer());
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
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().building(), buildings));
        }
        if (!paymentTypeCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentMethod().type(), paymentTypeCriteria));
        }
        if (!paymentStatusCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), paymentStatusCriteria));
        }

        int totalRows = Persistence.service().count(criteria);
        // query and return the result
        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, criteria, AttachLevel.Attached);
        Vector<PaymentRecordForReportDTO> paymentRecordsPageData = new Vector<PaymentRecordForReportDTO>();
        while (i.hasNext()) {
            paymentRecordsPageData.add(makeDTO(i.next()));
        }

        EntitySearchResult<PaymentRecordForReportDTO> result = new EntitySearchResult<PaymentRecordForReportDTO>();
        result.setTotalRows(totalRows);
        result.setData(paymentRecordsPageData);
        result.hasMoreData(result.getTotalRows() > (pageNumber * pageSize + paymentRecordsPageData.size())); // MIN(pageNumber) = 0, so the formula is correct 

        callback.onSuccess(result);
    }

    @Override
    public void paymentsSummary(AsyncCallback<EntitySearchResult<PaymentsSummary>> callback, Vector<Building> buildings, LogicalDate targetDate,
            Vector<PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize, Vector<Sort> sortingCriteria) {

        Vector<PaymentsSummary> summariesVector = new Vector<PaymentsSummary>();

        Iterator<Building> buildingIterator = !buildings.isEmpty() ? buildings.iterator() : Persistence.service().query(null,
                EntityQueryCriteria.create(Building.class), AttachLevel.Detached);
        Iterator<MerchantAccount> merchantAccounts = merchantAccountIterator(buildingIterator);

        PaymentsSummaryHelper summaryHelper = new PaymentsSummaryHelper();

        while (merchantAccounts.hasNext()) {
            MerchantAccount merchantAccount = merchantAccounts.next();
            for (PaymentStatus paymentStatus : paymentStatusCriteria) {
                PaymentsSummary summary = summaryHelper.calculateSummary(merchantAccount, paymentStatus, targetDate);
                if (summaryHelper.hasPayments(summary)) {
                    summariesVector.add(summary);
                }
            }
        }
        // load detached merchant accounts
        for (PaymentsSummary paymentsSummary : summariesVector) {
            paymentsSummary.merchantAccount().set(Persistence.service().retrieve(MerchantAccount.class, paymentsSummary.merchantAccount().getPrimaryKey()));
        }
        EntityListCriteria<PaymentsSummary> criteria = EntityListCriteria.create(PaymentsSummary.class);
        criteria.setSorts(sortingCriteria);
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(pageSize);

        InMemeoryListService<PaymentsSummary> inMemoryService = new InMemeoryListService<PaymentsSummary>(summariesVector);
        inMemoryService.list(callback, criteria);
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
        Persistence.service().retrieve(paymentRecordDBO.merchantAccount());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit());
        Persistence.service().retrieve(paymentRecordDBO.billingAccount().lease().unit().building());
        Persistence.service().retrieve(paymentRecordDBO.paymentMethod().customer());

        PaymentRecordForReportDTO paymentRecordDTO = dtoBinder.createDTO(paymentRecordDBO);

        return paymentRecordDTO;
    }

    /** returns iterator over merchant accounts of the given buildings */
    private static Iterator<MerchantAccount> merchantAccountIterator(Iterator<Building> buildingIterator) {
        List<MerchantAccount> merchantAccounts = new LinkedList<MerchantAccount>();
        Set<Key> alreadyAddedMerchantAccounts = new HashSet<Key>();

        while (buildingIterator.hasNext()) {
            EntityQueryCriteria<BuildingMerchantAccount> criteria = EntityQueryCriteria.create(BuildingMerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), buildingIterator.next()));

            List<BuildingMerchantAccount> buildingMerchantAccounts = Persistence.service().query(criteria, AttachLevel.Detached);
            for (BuildingMerchantAccount buildingMerchantAccount : buildingMerchantAccounts) {
                if (alreadyAddedMerchantAccounts.add(buildingMerchantAccount.merchantAccount().getPrimaryKey())) {
                    merchantAccounts.add(buildingMerchantAccount.merchantAccount().<MerchantAccount> detach());
                }
            }
        }
        return merchantAccounts.iterator();
    }

}
