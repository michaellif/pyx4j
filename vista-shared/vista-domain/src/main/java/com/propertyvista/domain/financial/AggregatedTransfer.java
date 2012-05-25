/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.PaymentRecord.PaidRejectedAggregatedTransferId;
import com.propertyvista.domain.financial.PaymentRecord.ReturnAggregatedTransferId;

public interface AggregatedTransfer extends IEntity {

    @I18n
    enum AggregatedTransferStatus {

        Rejected,

        Paid,

        Hold,

        Resent;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    };

    IPrimitive<LogicalDate> paymentDate();

    IPrimitive<AggregatedTransferStatus> status();

    MerchantAccount merchantAccount();

    @NotNull
    @Format("#0.00")
    IPrimitive<BigDecimal> amount();

    IPrimitive<Integer> grossPaymentCount();

    @Format("#0.00")
    IPrimitive<BigDecimal> rejectItemsAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> rejectItemsFee();

    IPrimitive<Integer> rejectItemsCount();

    @Format("#0.00")
    IPrimitive<BigDecimal> returnItemsAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> returnItemsFee();

    IPrimitive<Integer> returnItemsCount();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = PaymentRecord.class, mappedBy = PaidRejectedAggregatedTransferId.class)
    ISet<PaymentRecord> payments();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = PaymentRecord.class, mappedBy = ReturnAggregatedTransferId.class)
    ISet<PaymentRecord> returnedPayments();

    IPrimitive<String> transactionErrorMessage();
}
