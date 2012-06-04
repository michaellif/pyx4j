/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import static com.pyx4j.gwt.server.DateUtils.detectDateformat;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;

public class PaymentsSummaryHelperTestBase extends VistaDBTestBase {

    protected Lease lease;

    protected MerchantAccount merchantAccountA;

    protected MerchantAccount merchantAccountB;

    protected PaymentRecord makePaymentRecord(MerchantAccount merchantAccount, String lastStatusChangeDate, String amount, PaymentType paymentType,
            PaymentRecord.PaymentStatus paymentStatus) {

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);

        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(detectDateformat(lastStatusChangeDate)));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentMethod().type().setValue(paymentType);
        paymentRecord.paymentStatus().setValue(paymentStatus);
        paymentRecord.billingAccount().set(lease.billingAccount());

        Persistence.service().persist(paymentRecord);

        return paymentRecord;
    }

}
