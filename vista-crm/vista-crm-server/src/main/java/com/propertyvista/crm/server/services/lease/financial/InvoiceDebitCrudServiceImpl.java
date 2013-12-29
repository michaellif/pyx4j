/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.financial;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceDebitDTO;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceDebitCrudService;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

public class InvoiceDebitCrudServiceImpl implements InvoiceDebitCrudService {

    @Override
    public void retrieve(AsyncCallback<InvoiceDebitDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        InvoiceDebit debitDbo = Persistence.secureRetrieve(InvoiceDebit.class, entityId);

        EntityQueryCriteria<DebitCreditLink> debitCreditLinksCriteria = EntityQueryCriteria.create(DebitCreditLink.class);
        debitCreditLinksCriteria.eq(debitCreditLinksCriteria.proto().debitItem(), debitDbo);
        debitCreditLinksCriteria.desc(debitCreditLinksCriteria.proto().creditItem().postDate());
        debitCreditLinksCriteria.desc(debitCreditLinksCriteria.proto().creditItem().description());

        List<DebitCreditLink> debitCreditLinks = Persistence.service().query(debitCreditLinksCriteria);

        InvoiceDebitDTO debitDto = EntityFactory.create(InvoiceDebitDTO.class);
        debitDto.setPrimaryKey(debitDbo.getPrimaryKey());
        debitDto.date().setValue(debitDbo.postDate().getValue());
        debitDto.item().setValue(debitDbo.description().getValue());
        debitDto.totalAmount().setValue(debitDbo.amount().getValue());
        debitDto.outstandingDebit().setValue(debitDbo.outstandingDebit().getValue());
        for (DebitCreditLink debitCreditLink : debitCreditLinks) {
            debitDto.debitCreditLinks().add(DebitCreditLinkDtoConverter.asDto(debitCreditLink));
        }
        callback.onSuccess(debitDto);
    }

    @Override
    public void init(AsyncCallback<InvoiceDebitDTO> callback, InitializationData initializationData) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void create(AsyncCallback<Key> callback, InvoiceDebitDTO editableEntity) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, InvoiceDebitDTO editableEntity) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<InvoiceDebitDTO>> callback, EntityListCriteria<InvoiceDebitDTO> criteria) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("not implemented");
    }
}
