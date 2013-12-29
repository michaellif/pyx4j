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

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.crm.rpc.dto.lease.financial.DebitLinkDTO;
import com.propertyvista.domain.financial.billing.DebitCreditLink;

public class DebitCreditLinkDtoConverter {

    public static DebitLinkDTO asDto(DebitCreditLink debitCreditLink) {
        DebitLinkDTO debitLinkDto = EntityFactory.create(DebitLinkDTO.class);
        debitLinkDto.setPrimaryKey(debitCreditLink.getPrimaryKey());
        debitLinkDto.debitItemStub().set(debitCreditLink.debitItem().createIdentityStub());
        debitLinkDto.creditItemStub().set(debitCreditLink.creditItem().createIdentityStub());
        debitLinkDto.date().setValue(debitCreditLink.debitItem().postDate().getValue());
        debitLinkDto.arCodeType().setValue(debitCreditLink.debitItem().arCode().type().getValue());
        debitLinkDto.arCode().setValue(debitCreditLink.debitItem().arCode().name().getValue());
        debitLinkDto.description().setValue(debitCreditLink.debitItem().description().getValue());
        debitLinkDto.debitAmount().setValue(debitCreditLink.debitItem().amount().getValue());
        debitLinkDto.outstandingAmount().setValue(debitCreditLink.debitItem().outstandingDebit().getValue());
        debitLinkDto.paidAmount().setValue(debitCreditLink.amount().getValue());
        return debitLinkDto;
    }

}
