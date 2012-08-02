/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Vector;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.property.asset.unit.AptUnit;

@ToStringFormat("{0}, {1}, {2}, {3}")
public interface Lease2 extends IEntity {

    @I18n(context = "Lease Status")
    @XmlType(name = "LeaseStatus")
    public enum Status {

        /**
         * Existing (imported) lease that is in was just created but pending approval to become 'Active' and participate in billing.
         */
        Created,

        Application,

        Approved, // Application

        Cancelled, //TODO Implement (unreserve unit)

        Active,

        Completed, // Lease end date is passed

        Closed; // All the transactions completed and lease closed

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        // state sets:

        public static Collection<Status> draft() {
            return EnumSet.of(Created, Application);
        }

        public static Collection<Status> active() {
            return EnumSet.of(Approved, Active);
        }

        public static Collection<Status> current() {
            return EnumSet.of(Approved, Active, Completed);
        }

        public static Collection<Status> currentNew() {
            Vector<Status> set = new Vector<Status>(current());
            set.add(Created);
            return set;
        }

        public static Collection<Status> former() {
            return EnumSet.of(Completed, Closed);
        }

        // states:

        public boolean isDraft() {
            return draft().contains(this);
        }

        public boolean isActive() {
            return active().contains(this);
        }

        public boolean isCurrent() {
            return current().contains(this);
        }

        public boolean isFormer() {
            return former().contains(this);
        }
    }

    @I18n(context = "Lease Completion Type")
    @XmlType(name = "LeaseCompletionType")
    public enum CompletionType {

        Notice,

        LegalVacate,

        Skip,

        Eviction;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Payment Frequency")
    @XmlType(name = "PaymentFrequency")
    public enum PaymentFrequency {
        Monthly(28, 15), Weekly(7, 5), SemiMonthly(14, 10), BiWeekly(14, 10), SemiAnnyally(182, -1), Annually(365, -1);

        private final int numOfCycles;

        private final int billRunTargetDayOffset;

        PaymentFrequency(int numOfCycles, int billRunTargetDayOffset) {
            this.numOfCycles = numOfCycles;
            this.billRunTargetDayOffset = billRunTargetDayOffset;
        }

        public int getNumOfCycles() {
            return numOfCycles;
        }

        public int getBillRunTargetDayOffset() {
            return billRunTargetDayOffset;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // Data: ----------------------------------------------

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Id")
    IPrimitive<String> leaseId();

    @NotNull
    @ReadOnly
    @ToString(index = 1)
    @MemberColumn(name = "leaseType")
    IPrimitive<Service.ServiceType> type();

    @NotNull
    IPrimitive<PaymentFrequency> paymentFrequency();

    @NotNull
    @ToString(index = 2)
    @Caption(name = "Selected Unit")
    AptUnit unit();

    // lease begin/end: 

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseFrom();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseTo();

    // other dates:

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> actualLeaseTo();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> expectedMoveIn();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> expectedMoveOut();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> actualMoveIn();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> actualMoveOut();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> moveOutNotice();

//TODO _2
//    @JoinColumn
//    @Owned(forceCreation = true, cascade = {})
//    BillingAccount billingAccount();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();

    // internals:

    @ToString(index = 3)
    IPrimitive<Status> status();

    IPrimitive<CompletionType> completion();

    @ReadOnly
    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> creationDate();

    @Timestamp(Update.Updated)
    IPrimitive<Date> updated();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> approvalDate();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> activationDate();

    @Owned
    LeaseApplication2 leaseApplication();

    @Owned(cascade = {})
    IList<LeaseTerm> leaseTerms();

    LeaseTerm currentLeaseTerm();
}
