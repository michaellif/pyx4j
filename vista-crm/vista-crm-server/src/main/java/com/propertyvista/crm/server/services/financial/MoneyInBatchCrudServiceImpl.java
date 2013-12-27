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
import java.util.List;
import java.util.Random;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipPaymentRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;

public class MoneyInBatchCrudServiceImpl implements MoneyInBatchCrudService {

    @Override
    public void list(AsyncCallback<EntitySearchResult<MoneyInBatchDTO>> callback, EntityListCriteria<MoneyInBatchDTO> criteria) {
        // TODO replace this stub with implementation
        new InMemeoryListService<MoneyInBatchDTO>(makeMokupBatches()).list(callback, criteria);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new RuntimeException("Not Implemented"); // TODO implement remove batch for not yet posted batches
    }

    @Override
    public void retrieve(AsyncCallback<MoneyInBatchDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        // TODO replace mockup with code
        callback.onSuccess(makeMokupBatches().get(0));
    }

    @Override
    public void init(AsyncCallback<MoneyInBatchDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new RuntimeException("Operation NOT supported");
    }

    @Override
    public void create(AsyncCallback<Key> callback, MoneyInBatchDTO editableEntity) {
        throw new RuntimeException("Operation NOT supported");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, MoneyInBatchDTO editableEntity) {
        throw new RuntimeException("Operation NOT supported");
    }

    List<MoneyInBatchDTO> makeMokupBatches() {
        List<MoneyInBatchDTO> mockupBatches = new ArrayList<MoneyInBatchDTO>();

        MoneyInBatchDTO mockupBatch = EntityFactory.create(MoneyInBatchDTO.class);
        mockupBatch.setPrimaryKey(new Key(1));
        mockupBatch.building().setValue("bath1660");

        mockupBatch.depositSlipNumber().setValue(42);
        mockupBatch.depositDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-12-31")));

        mockupBatch.bankAccountName().setValue("True North Properties");
        mockupBatch.bankId().setValue("12");
        mockupBatch.bankTransitNumber().setValue("5123");
        mockupBatch.bankAccountNumber().setValue("123456");

        mockupBatch.isPosted().setValue(false);
        mockupBatch.totalReceivedAmount().setValue(new BigDecimal("1000"));
        for (int i = 1; i < 100; ++i) {
            DepositSlipPaymentRecordDTO payment = mockupBatch.payments().$();
            payment.unit().setValue("" + (100 + i));
            payment.tenantId().setValue("t01204" + (100 + i));
            payment.tenantName().setValue("Tenant Tenantovic #" + i);
            payment.checkNumber().setValue("" + new Random().nextInt(200));
            payment.amount().setValue(new BigDecimal(1000 + new Random().nextInt(200)));
            mockupBatch.totalReceivedAmount().setValue(mockupBatch.totalReceivedAmount().getValue().add(payment.amount().getValue()));
            mockupBatch.payments().add(payment);
        }
        mockupBatch.numberOfReceipts().setValue(mockupBatch.payments().size());
        mockupBatches.add(mockupBatch);
        return mockupBatches;
    }

}
