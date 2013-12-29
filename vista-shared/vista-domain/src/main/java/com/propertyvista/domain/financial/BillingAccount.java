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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface BillingAccount extends IEntity {

    final static Logger log = LoggerFactory.getLogger(BillingAccount.class);

    @I18n(context = "Payment Frequency")
    @XmlType(name = "PaymentFrequency")
    public enum BillingPeriod {

        Monthly(28),

        Weekly(7),

        SemiMonthly(14),

        BiWeekly(14),

        SemiAnnyally(182),

        Annually(365);

        private final int numOfCycles;

        BillingPeriod(int numOfCycles) {
            this.numOfCycles = numOfCycles;
        }

        public int getNumOfCycles() {
            return numOfCycles;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public enum ProrationMethod {
        Actual, Standard, Annual
    }

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

        public int paymentCode() {
            return paymentCode;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static PaymentAccepted getPaymentType(int paymentCode) {
            for (PaymentAccepted code : PaymentAccepted.values()) {
                if (code.paymentCode() == paymentCode) {
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
    @Indexed(uniqueConstraint = true)
    Lease lease();

    /**
     * Immediate InvoiceLineItems are kept here between billing runs. Approved billing cleans this set.
     */

    //TODO - see @Comments for new labels as per VISTA-2605 - alexs

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<InvoiceLineItem> invoiceLineItems();

    @NotNull
    IPrimitive<BillingPeriod> billingPeriod();

    BillingType billingType();

    @Caption(name = "Payment Due Day", description = "Number of days between Billing Period Start Date and Payment Due Date")
    IPrimitive<Integer> paymentDueDayOffset();

    @Caption(name = "Final Bill Due Day", description = "Final Bill payment Due day, relative to Lease End Day")
    IPrimitive<Integer> finalDueDayOffset();

    IPrimitive<ProrationMethod> prorationMethod();

    // move to InternalBillingAccount when $asInstanceOf  implemented
    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<LeaseArrearsSnapshot> arrearsSnapshots();

    @Length(14)
    @Indexed(uniqueConstraint = true)
    IPrimitive<String> accountNumber();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<PaymentRecord> payments();

    IPrimitive<PaymentAccepted> paymentAccepted();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<Bill> bills();

    /**
     * Counter for all (including failed) bills for given lease
     * 
     * @return
     */
    IPrimitive<Integer> billCounter();

    @Owned
    @OrderBy(LeaseAdjustment.OrderId.class)
    @Caption(name = "Lease Adjustments")
    @Detached()
    IList<LeaseAdjustment> adjustments();

    @Owned(cascade = {})
    @Detached
    IList<DepositLifecycle> deposits();

    //Should have deposit value field

    // atb report

    /**
     * for newly created/converted existing leases:
     */
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Initial Balance")
    IPrimitive<BigDecimal> carryforwardBalance();

//    TODO VladS Fix Me
//    @Override
//    @Owned(cascade = {})
//    @Detached(level = AttachLevel.Detached)
//    ISet<LeaseArrearsSnapshot> arrearsSnapshots();

}
