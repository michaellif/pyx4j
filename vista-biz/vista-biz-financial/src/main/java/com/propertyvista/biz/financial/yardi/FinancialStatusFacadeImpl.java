/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.yardi;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.dto.LeaseYardiFinancialInfoDTO;

public class FinancialStatusFacadeImpl implements FinancialStatusFacade {

    @Override
    public LeaseYardiFinancialInfoDTO getFinancialStatus(YardiBillingAccount billingAccountIdentityStub) {
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.eq(criteria.proto().billingAccount(), billingAccountIdentityStub);
        criteria.asc(criteria.proto().postDate());

        List<InvoiceLineItem> invoiceLineItems = Persistence.service().query(criteria);

        LeaseYardiFinancialInfoDTO financialStatus = EntityFactory.create(LeaseYardiFinancialInfoDTO.class);
        for (InvoiceLineItem invoiceLineItem : invoiceLineItems) {
            if (invoiceLineItem.isInstanceOf(YardiPayment.class)) {
                financialStatus.payments().add(invoiceLineItem.duplicate(YardiPayment.class));
            } else if (invoiceLineItem.isInstanceOf(YardiCharge.class)) {
                financialStatus.charges().add(invoiceLineItem.duplicate(YardiCharge.class));
            }
        }

        return financialStatus;
    }
}
