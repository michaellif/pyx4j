/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.PaymentRecord;

public class PaymentRecordListServiceImpl extends AbstractListServiceImpl<PaymentRecord> implements PaymentRecordListService {

    public PaymentRecordListServiceImpl() {
        super(PaymentRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecord dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().building());
        Persistence.service().retrieve(dto.paymentMethod().customer());
    }
}
