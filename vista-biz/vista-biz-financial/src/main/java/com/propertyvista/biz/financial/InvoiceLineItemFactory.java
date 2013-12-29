/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class InvoiceLineItemFactory {

    static public InvoiceAccountCharge createInvoiceAccountCharge(LeaseAdjustment adjustment) {
        InvoiceAccountCharge charge = EntityFactory.create(InvoiceAccountCharge.class);

        charge.billingAccount().set(adjustment.billingAccount());
        charge.amount().setValue(adjustment.amount().getValue());
        charge.adjustment().set(adjustment);
        charge.description().setValue(adjustment.code().name().getValue());
        charge.arCode().set(adjustment.code());

        Persistence.service().retrieve(adjustment.billingAccount());
        Persistence.service().retrieve(adjustment.billingAccount().lease());
        Persistence.service().retrieve(adjustment.billingAccount().lease().unit());
        Persistence.service().retrieve(adjustment.billingAccount().lease().unit().building());
        TaxUtils.calculateAccountChargeTax(charge, adjustment.billingAccount().lease().unit().building());

        return charge;

    }

    static public InvoiceAccountCredit createInvoiceAccountCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = EntityFactory.create(InvoiceAccountCredit.class);

        credit.billingAccount().set(adjustment.billingAccount());
        credit.amount().setValue(adjustment.amount().getValue().negate());
        credit.adjustment().set(adjustment);
        credit.description().setValue(adjustment.code().name().getValue());

        return credit;
    }

}
