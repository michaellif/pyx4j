/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInPaymentDTO;
import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class MoneyInBatchCreateDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(MoneyInBatchCreateDeferredProcess.class);

    private static final long serialVersionUID = 4099464017607928359L;

    private final LogicalDate receiptDate;

    private final List<MoneyInPaymentDTO> payments;

    private volatile Throwable error;

    public MoneyInBatchCreateDeferredProcess(LogicalDate receiptDate, Vector<MoneyInPaymentDTO> payments) {
        progress.progressMaximum.set(payments.size());
        this.receiptDate = receiptDate;
        this.payments = payments;
    }

    @Override
    public void execute() {
        try {
            Map<Building, Collection<MoneyInPaymentDTO>> batchesMap = partitionPayments();
            for (Entry<Building, Collection<MoneyInPaymentDTO>> buildingPayments : batchesMap.entrySet()) {
                createBatch(buildingPayments.getKey(), buildingPayments.getValue());
            }
        } catch (Throwable e) {
            log.error("Failed to create MoneyIn Batches", e);
            this.error = e;
        } finally {
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        if (error != null) {
            r.setError();
            r.setErrorStatusMessage(error.getMessage());
        }
        return r;
    }

    private Map<Building, Collection<MoneyInPaymentDTO>> partitionPayments() {
        Map<Building, Collection<MoneyInPaymentDTO>> partition = new HashMap<Building, Collection<MoneyInPaymentDTO>>();
        for (MoneyInPaymentDTO payment : payments) {
            Building building = building(payment);
            Collection<MoneyInPaymentDTO> batchPayments = partition.get(building);
            if (batchPayments == null) {
                batchPayments = new LinkedList<MoneyInPaymentDTO>();
                partition.put(building, batchPayments);
            }
            batchPayments.add(payment);
        }
        return partition;
    }

    private Building building(MoneyInPaymentDTO payment) {
        Lease lease = Persistence.service().retrieve(Lease.class, payment.leaseIdStub().getPrimaryKey());
        Persistence.service().retrieve(lease.unit().building());
        Building building = lease.unit().building().createIdentityStub();
        return building;
    }

    private PaymentPostingBatch createBatch(Building buildingIdStub, Collection<MoneyInPaymentDTO> payments) throws PaymentException {
        PaymentPostingBatch batch = ServerSideFactory.create(PaymentFacade.class).createPostingBatch(buildingIdStub, receiptDate);

        for (MoneyInPaymentDTO paymentDto : payments) {
            createPaymentRecord(paymentDto, batch);
            progress.progress.incrementAndGet();
        }

        return batch;
    }

    private PaymentRecord createPaymentRecord(MoneyInPaymentDTO dto, PaymentPostingBatch batch) throws PaymentException {
        Lease lease = Persistence.service().retrieve(Lease.class, dto.leaseIdStub().getPrimaryKey());
        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        if (!dto.paymentReceiptDate().isNull()) {
            paymentRecord.receivedDate().setValue(dto.paymentReceiptDate().getValue());
        } else {
            paymentRecord.receivedDate().setValue(receiptDate);
        }
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.leaseTermParticipant().set(dto.payerLeaseTermTenantIdStub());
        paymentRecord.amount().setValue(dto.payedAmount().getValue());

        LeaseTermTenant termTenant = Persistence.service().retrieve(LeaseTermTenant.class, dto.payerLeaseTermTenantIdStub().getPrimaryKey());
        paymentRecord.paymentMethod().isProfiledMethod().setValue(false);
        paymentRecord.paymentMethod().customer().set(termTenant.leaseParticipant().customer());
        paymentRecord.paymentMethod().type().setValue(PaymentType.Check);
        CheckInfo checkInfo = EntityFactory.create(CheckInfo.class);
        checkInfo.nameOn().setValue(termTenant.leaseParticipant().customer().person().name().getStringView());
        checkInfo.checkNo().setValue(dto.checkNumber().getValue());
        paymentRecord.paymentMethod().details().set(checkInfo);

        ServerSideFactory.create(PaymentFacade.class)
                .validatePaymentMethod(paymentRecord.billingAccount(), paymentRecord.paymentMethod(), VistaApplication.crm);
        ServerSideFactory.create(PaymentFacade.class).validatePayment(paymentRecord, VistaApplication.crm);

        paymentRecord.batch().set(batch);

        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        return paymentRecord;
    }
}
