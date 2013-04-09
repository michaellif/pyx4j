/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.financial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.crm.rpc.dto.lease.financial.DebitLinkDTO;
import com.propertyvista.crm.rpc.dto.lease.financial.InvoiceCreditDTO;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceCreditCrudService;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;

public class InvoiceCreditCrudServiceImpl implements InvoiceCreditCrudService {

    @Override
    public void retrieve(AsyncCallback<InvoiceCreditDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTraget retrieveTraget) {
        InvoiceCredit creditDbo = Persistence.secureRetrieve(InvoiceCredit.class, entityId);
        Persistence.service().retrieveMember(creditDbo.debitLinks(), AttachLevel.Attached);

        List<DebitCreditLink> debitCreditLinks = new ArrayList<DebitCreditLink>(creditDbo.debitLinks());
        Collections.sort(debitCreditLinks, new Comparator<DebitCreditLink>() {
            @Override
            public int compare(DebitCreditLink o1, DebitCreditLink o2) {
                int cmp = o1.creditItem().postDate().getValue().compareTo(o2.creditItem().postDate().getValue());
                if (cmp != 0) {
                    cmp = o1.creditItem().description().getValue().compareTo(o2.creditItem().description().getValue());
                }
                return cmp;
            }
        });

        InvoiceCreditDTO creditDto = EntityFactory.create(InvoiceCreditDTO.class);
        creditDbo.setPrimaryKey(creditDbo.getPrimaryKey());
        creditDto.date().setValue(creditDbo.postDate().getValue());
        creditDto.item().setValue(creditDbo.description().getValue());
        creditDto.totalAmount().setValue(creditDbo.amount().getValue().abs());
        creditDto.outstandingCredit().setValue(creditDbo.outstandingCredit().getValue().abs());
        for (DebitCreditLink debitCreditLink : debitCreditLinks) {
            DebitLinkDTO debitLinkDto = EntityFactory.create(DebitLinkDTO.class);
            debitLinkDto.setPrimaryKey(debitCreditLink.getPrimaryKey());
            debitLinkDto.date().setValue(debitCreditLink.debitItem().postDate().getValue());
            debitLinkDto.arCodeType().setValue(debitCreditLink.debitItem().arCode().type().getValue());
            debitLinkDto.description().setValue(debitCreditLink.debitItem().description().getValue());
            debitLinkDto.outstandingAmount().setValue(debitCreditLink.debitItem().amount().getValue());
            debitLinkDto.outstandingAmount().setValue(debitCreditLink.debitItem().outstandingDebit().getValue());
            debitLinkDto.paidAmount().setValue(debitCreditLink.amount().getValue());
            creditDto.debitCreditLinks().add(debitLinkDto);
        }

        callback.onSuccess(creditDto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, InvoiceCreditDTO editableEntity) {
        throw new IllegalStateException("not supported");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, InvoiceCreditDTO editableEntity) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<InvoiceCreditDTO>> callback, EntityListCriteria<InvoiceCreditDTO> criteria) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new IllegalStateException("not supported");
    }

}
