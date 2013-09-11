/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billingext.ExternalBillingFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
import com.propertyvista.domain.financial.billingext.dto.PaymentDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public class ExternalTestBase extends LeaseFinancialTestBase {

    public void postExternalCharge(String amount, String description, String fromDate, String toDate) {
        ChargeDTO charge = EntityFactory.create(ChargeDTO.class);
        charge.amount().setValue(new BigDecimal(amount));
        charge.description().setValue(description);
        charge.transactionDate().setValue(new LogicalDate());
        charge.fromDate().setValue(getDate(fromDate));
        charge.toDate().setValue(getDate(toDate));

        ServerSideFactory.create(ExternalBillingFacade.class).postCharge(charge, retrieveLease().leaseId().getValue());
    }

    public void postExternalPayment(String amount, String description, String type) {
        PaymentDTO payment = EntityFactory.create(PaymentDTO.class);
        payment.amount().setValue(new BigDecimal(amount));
        payment.description().setValue(description);
        payment.transactionDate().setValue(new LogicalDate());
        payment.paymentType().setValue(type);

        ServerSideFactory.create(ExternalBillingFacade.class).postPayment(payment, retrieveLease().leaseId().getValue());
    }

    protected Bill runExternalBilling() {
        Lease lease = retrieveLease();

        DataDump.dump("leaseT", lease);

        Bill bill = ServerSideFactory.create(ExternalBillingFacade.class).runBilling(lease);

        Persistence.service().commit();

        return bill;
    }

    @Override
    protected Bill approveApplication(boolean printBill) {
        // do nothing; for external billing use activateLease()
        return ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease()); //super.approveApplication(printBill);
    }

    @Override
    protected void activateLease() {
        Lease lease = retrieveLeaseDraft();

        lease.status().setValue(Lease.Status.Approved);
        ServerSideFactory.create(LeaseFacade.class).finalize(lease);
        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());

        super.activateLease();
    }
}
