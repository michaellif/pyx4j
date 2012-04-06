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

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease.LeaseV;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;

@ToStringFormat("{0}, {1}, {2}, {3}")
public interface Lease extends IVersionedEntity<LeaseV> {

    @I18n(context = "Lease Status")
    @XmlType(name = "LeaseStatus")
    public enum Status {

        Created,

        ApplicationInProgress,

        Approved, // Application

        Active,

        Completed, // Lease end date is passed

        FinalBillIssued, // Final bill is issued

        Closed; // All the transactions completed and lease closed

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public boolean isCurrent() {
            return this == Approved || this == Active || this == Completed || this == FinalBillIssued;
        }

        public boolean isDraft() {
            return this == Created || this == ApplicationInProgress;
        }

        //TODO find better name
        public boolean isClosed() {
            return this == Completed || this == Closed;
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

    @I18n(context = "Lease Term")
    @XmlType(name = "LeaseTerm")
    public enum Term {

        Fixed,

        Periodic,

        Indefinite;

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
    IPrimitive<String> leaseId();

    @NotNull
    @ReadOnly
    @ToString(index = 1)
    @MemberColumn(name = "leaseType")
    IPrimitive<Service.Type> type();

    @NotNull
    @Detached
    @ToString(index = 2)
    @Caption(name = "Selected Unit")
    AptUnit unit();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseFrom();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseTo();

    @NotNull
    IPrimitive<PaymentFrequency> paymentFrequency();

    @JoinColumn
    @Owned(forceCreation = true)
    BillingAccount billingAccount();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();

    // internals:

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    @Timestamp
    IPrimitive<Date> updated();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> approvalDate();

    @Detached
    @Deprecated
    MasterOnlineApplication application();

    @Owned
    LeaseApplication leaseApplication();

    // Versioned part:

    public interface LeaseV extends IVersionData<Lease> {

        @ToString(index = 3)
        IPrimitive<Status> status();

        IPrimitive<CompletionType> completion();

        // various dates:

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

        @Owned
        @Detached
        @OrderBy(TenantInLease.OrderInLeaseId.class)
        IList<TenantInLease> tenants();

        @EmbeddedEntity
        LeaseProducts leaseProducts();
    }
}
