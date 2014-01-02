/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.CustomerCreditCheckCrudService;
import com.propertyvista.domain.pmc.payment.CustomerCreditCheckTransaction;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.server.TaskRunner;

public class CustomerCreditCheckCrudServiceImpl extends AbstractCrudServiceDtoImpl<CustomerCreditCheck, CustomerCreditCheckDTO> implements
        CustomerCreditCheckCrudService {

    public CustomerCreditCheckCrudServiceImpl() {
        super(CustomerCreditCheck.class, CustomerCreditCheckDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(CustomerCreditCheck bo, CustomerCreditCheckDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(to.screening());
        Persistence.service().retrieveMember(to.screening().screene());

        if (!to.transactionId().isNull()) {
            final Key transactionId = to.transactionId().getValue();
            to.transactionRef().setValue(transactionId.toString());
            to.transaction().set(TaskRunner.runInOperationsNamespace(new Callable<CustomerCreditCheckTransaction>() {
                @Override
                public CustomerCreditCheckTransaction call() {
                    CustomerCreditCheckTransaction transaction = Persistence.service().retrieve(CustomerCreditCheckTransaction.class, transactionId);
                    Persistence.service().retrieve(transaction.paymentMethod());
                    return transaction;
                }
            }));
        }
    }

    @Override
    protected void enhanceListRetrieved(CustomerCreditCheck entity, CustomerCreditCheckDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        enhanceRetrieved(entity, dto, RetrieveTarget.View);
    }

}
