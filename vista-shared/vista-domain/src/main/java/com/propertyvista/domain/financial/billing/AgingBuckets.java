/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public interface AgingBuckets extends IEntity {

    IPrimitive<DebitType> debitType();

    /** sum of money that is owed but which time is not yet overdue */
    @Format("#,##0.00")
    @Caption(name = "Current")
    IPrimitive<BigDecimal> bucketCurrent();

    /** sum of payments that is due from 1 day to the day when the month and were not payed */
    @Format("#,##0.00")
    @Caption(name = "This Month")
    IPrimitive<BigDecimal> bucketThisMonth();

    /** sum of payments that haven't been received from client 1 to 30 days ago */
    @Format("#,##0.00")
    @Caption(name = "1 to 30")
    IPrimitive<BigDecimal> bucket30();

    /** sum of payments that haven't been received from client 31 to 60 days ago */
    @Format("#,##0.00")
    @Caption(name = "31 to 60")
    IPrimitive<BigDecimal> bucket60();

    /** sum of payments that haven't been received from client 61 to 90 days ago */
    @Format("#,##0.00")
    @Caption(name = "61 to 90")
    IPrimitive<BigDecimal> bucket90();

    /** sum of payments that haven't been received from client 91 and more days ago */
    @Format("#,##0.00")
    @Caption(name = "91 and more")
    IPrimitive<BigDecimal> bucketOver90();

    /**
     * sum of all the money that is owed by client (arrears + not arrears)
     */
    @Format("#,##0.00")
    @Caption(name = "AR Balance")
    // TODO rename column arrearsAmount to 'AR Balance' (this is AR Balance not just arrears)
    IPrimitive<BigDecimal> arrearsAmount();

    @Format("#,##0.00")
    @Caption(name = "Prepayments")
    // TODO rename column to prepayments
    IPrimitive<BigDecimal> creditAmount();

    @Format("#,##0.00")
    /**
     *  {@link #arrearsAmount} - {@link #creditAmount} 
     */
    IPrimitive<BigDecimal> totalBalance();

}
