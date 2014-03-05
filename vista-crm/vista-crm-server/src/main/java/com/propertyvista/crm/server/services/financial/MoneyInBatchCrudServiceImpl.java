/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipCheckDetailsRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CheckInfo;

public class MoneyInBatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<PaymentPostingBatch, MoneyInBatchDTO> implements MoneyInBatchCrudService {

    public MoneyInBatchCrudServiceImpl() {
        super(PaymentPostingBatch.class, MoneyInBatchDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bind(toProto.building(), boProto.building().propertyCode());
        bind(toProto.depositDate(), boProto.depositDetails().depositDate());
        bind(toProto.bankId(), boProto.depositDetails().merchantAccount().bankId());
        bind(toProto.bankTransitNumber(), boProto.depositDetails().merchantAccount().branchTransitNumber());
        bind(toProto.bankAccountNumber(), boProto.depositDetails().merchantAccount().accountNumber());
        bind(toProto.bankAccountName(), boProto.depositDetails().merchantAccount().accountName());
        bind(toProto.postingStatus(), boProto.status());
    }

    @Override
    protected void retrievedForList(PaymentPostingBatch bo) {
        super.retrievedForList(bo);
        Persistence.service().retrieve(bo.building());
    }

    @Override
    protected void enhanceListRetrieved(PaymentPostingBatch bo, MoneyInBatchDTO dto) {
        super.enhanceListRetrieved(bo, dto);

        dto.depositSlipNumber().setValue((int) bo.getPrimaryKey().asLong());

        Persistence.service().retrieveMember(bo.payments());

        BigDecimal total = new BigDecimal("0.00");
        int numberOfReceipts = 0;
        for (PaymentRecord paymentRecord : bo.payments()) {
            if (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Canceled) {
                total = total.add(paymentRecord.amount().getValue());
                numberOfReceipts++;
            }
        }
        dto.totalReceivedAmount().setValue(total);
        dto.numberOfReceipts().setValue(numberOfReceipts);
    }

    @Override
    protected void retrievedSingle(PaymentPostingBatch bo, RetrieveTarget retrieveTarget) {
        super.retrievedSingle(bo, retrieveTarget);
        Persistence.service().retrieve(bo.depositDetails().merchantAccount());
        Persistence.service().retrieve(bo.building());
    }

    @Override
    protected void enhanceRetrieved(PaymentPostingBatch bo, MoneyInBatchDTO to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        this.enhanceListRetrieved(bo, to);
        List<DepositSlipCheckDetailsRecordDTO> paymentsDto = new ArrayList<>(bo.payments().size());
        for (PaymentRecord paymentRecord : bo.payments()) {
            DepositSlipCheckDetailsRecordDTO paymentDetailsDto = to.payments().$();
            paymentDetailsDto.id().setValue(paymentRecord.id().getValue());
            paymentDetailsDto.status().setValue(paymentRecord.paymentStatus().getValue());
            Persistence.service().retrieve(paymentRecord.billingAccount());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            paymentDetailsDto.unit().set(paymentRecord.billingAccount().lease().unit().info().number());
            Persistence.service().retrieve(paymentRecord.leaseTermParticipant());
            paymentDetailsDto.tenantId().setValue(paymentRecord.leaseTermParticipant().leaseParticipant().participantId().getValue());
            paymentDetailsDto.tenantName().setValue(paymentRecord.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView());
            paymentDetailsDto.checkNumber().setValue(paymentRecord.paymentMethod().details().duplicate(CheckInfo.class).checkNo().getValue());
            paymentDetailsDto.amount().setValue(paymentRecord.amount().getValue());
            paymentDetailsDto.date().setValue(paymentRecord.targetDate().getValue());
            paymentsDto.add(paymentDetailsDto);
        }
        Collections.sort(paymentsDto, new Comparator<DepositSlipCheckDetailsRecordDTO>() {
            @Override
            public int compare(DepositSlipCheckDetailsRecordDTO o1, DepositSlipCheckDetailsRecordDTO o2) {
                return o1.unit().getValue().compareTo(o2.unit().getValue());
            }
        });
        to.payments().addAll(paymentsDto);

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void create(AsyncCallback<Key> callback, MoneyInBatchDTO editableEntity) {
        throw new RuntimeException("Operation NOT supported");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, MoneyInBatchDTO editableEntity) {
        PaymentPostingBatch postingBatch = Persistence.secureRetrieve(PaymentPostingBatch.class, editableEntity.getPrimaryKey());

        postingBatch.depositDetails().depositDate().setValue(editableEntity.depositDate().getValue());

        Persistence.secureSave(postingBatch);
        Persistence.service().commit();
        callback.onSuccess(postingBatch.getPrimaryKey());
    }

}
