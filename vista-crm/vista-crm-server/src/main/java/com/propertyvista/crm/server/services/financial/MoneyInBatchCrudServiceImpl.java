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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipCheckDetailsRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentPostingBatch.PostingStatus;
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
        bind(toProto.bankAccountName(), boProto.depositDetails().bankAccountName());
        bind(toProto.bankId(), boProto.depositDetails().bankId());
        bind(toProto.bankTransitNumber(), boProto.depositDetails().bankTransitNumber());
        bind(toProto.bankAccountNumber(), boProto.depositDetails().bankAccountNumber());
    }

    @Override
    protected void enhanceListRetrieved(PaymentPostingBatch bo, MoneyInBatchDTO dto) {
        super.enhanceListRetrieved(bo, dto);

        Persistence.service().retrieve(bo.building());
        dto.depositSlipNumber().setValue((int) bo.getPrimaryKey().asLong());

        dto.isPosted().setValue(bo.status().getValue() == PostingStatus.Posted);
        Persistence.service().retrieveMember(bo.payments());

        BigDecimal total = new BigDecimal("0.00");
        for (PaymentRecord paymentRecord : bo.payments()) {
            total = total.add(paymentRecord.amount().getValue());
        }
        dto.totalReceivedAmount().setValue(total);
        dto.numberOfReceipts().setValue(bo.payments().size());
    }

    @Override
    protected void enhanceRetrieved(PaymentPostingBatch bo, MoneyInBatchDTO to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        this.enhanceListRetrieved(bo, to);
        for (PaymentRecord paymentRecord : bo.payments()) {
            DepositSlipCheckDetailsRecordDTO paymentRecordDto = to.payments().$();
            Persistence.service().retrieve(paymentRecord.billingAccount());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            paymentRecordDto.unit().set(paymentRecord.billingAccount().lease().unit().info().number());
            Persistence.service().retrieve(paymentRecord.leaseTermParticipant());
            paymentRecordDto.tenantId().setValue(paymentRecord.leaseTermParticipant().leaseParticipant().participantId().getValue());
            paymentRecordDto.tenantName().setValue(paymentRecord.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView());
            paymentRecordDto.checkNumber().setValue(paymentRecord.paymentMethod().details().duplicate(CheckInfo.class).checkNo().getValue());
            paymentRecordDto.amount().setValue(paymentRecord.amount().getValue());
            paymentRecordDto.date().setValue(paymentRecord.targetDate().getValue());
            to.payments().add(paymentRecordDto);
        }

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new RuntimeException("Not Implemented"); // TODO implement remove batch for not yet posted batches
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
        postingBatch.depositDetails().bankAccountName().setValue(editableEntity.bankAccountName().getValue());
        postingBatch.depositDetails().bankId().setValue(editableEntity.bankId().getValue());
        postingBatch.depositDetails().bankTransitNumber().setValue(editableEntity.bankTransitNumber().getValue());
        postingBatch.depositDetails().bankAccountNumber().setValue(editableEntity.bankAccountNumber().getValue());

        Persistence.secureSave(postingBatch);
        Persistence.service().commit();
        callback.onSuccess(postingBatch.getPrimaryKey());
    }

}
