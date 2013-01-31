/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface BillingAccount extends IEntity {

    final static Logger log = LoggerFactory.getLogger(BillingAccount.class);

    @I18n(context = "Payment Accepted")
    @XmlType(name = "PaymentAccepted")
    public enum PaymentAccepted {
        Any(0),

        DoNotAccept(1),

        CashEquivalent(2);

        private final int paymentCode;

        PaymentAccepted(int paymentCode) {
            this.paymentCode = paymentCode;
        }

        private int value() {
            return paymentCode;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static PaymentAccepted getPaymentType(int paymentCode) {
            for (PaymentAccepted code : PaymentAccepted.values()) {
                if (code.value() == paymentCode) {
                    return code;
                }
            }
            return PaymentAccepted.Any;
        }

        public static PaymentAccepted getPaymentType(String paymentCode) {
            int code = Integer.parseInt(paymentCode);
            try {
                return getPaymentType(code);
            } catch (Exception e) {
                log.error("Error parsing string to int for PaymentAccepted", e);
                throw new Error("Error parsing string to int for PaymentAccepted", e);
            }
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    Lease lease();

    /**
     * Immediate InvoiceLineItems are kept here between billing runs. Approved billing cleans this set.
     */
    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<InvoiceLineItem> invoiceLineItems();

    // TODO move to InternalBillingAccount when $asInstanceOf  implemented
    @ReadOnly(allowOverrideNull = true)
    BillingType billingType();

    @Length(14)
    @Indexed(uniqueConstraint = true)
    IPrimitive<String> accountNumber();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<PaymentRecord> payments();

    IPrimitive<PaymentAccepted> paymentAccepted();
}
