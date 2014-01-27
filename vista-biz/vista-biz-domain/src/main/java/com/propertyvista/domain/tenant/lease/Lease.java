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
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.shared.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.property.asset.unit.AptUnit;

@ToStringFormat("{0}, {1}, {2}, {3}")
@DiscriminatorValue("Lease")
public interface Lease extends IEntity, HasNotesAndAttachments {

    @I18n(context = "Lease Status")
    @XmlType(name = "LeaseStatus")
    public enum Status {

        /**
         * Existing/Imported lease which is created but pending approval to become 'Active' and participate in billing.
         */
        @Translate("Current Lease")
        ExistingLease,

        /**
         * Regular lease which is created without Application phase. Pending approval to become 'Active' and participate in billing.
         */
        NewLease,

        /**
         * Typical flow lease start point
         */
        Application,

        /**
         * For Application
         */
        Approved,

        Cancelled,

        Active,

        /**
         * {@link #leaseTo} is passed
         */
        Completed,

        /**
         * All the transactions completed and lease closed
         */
        Closed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        // state sets:

        public static EnumSet<Status> draft() {
            return EnumSet.of(ExistingLease, NewLease, Application);
        }

        public static EnumSet<Status> present() {
            return EnumSet.of(ExistingLease, NewLease, Approved, Active, Completed);
        }

        public static EnumSet<Status> active() {
            return EnumSet.of(Approved, Active);
        }

        public static EnumSet<Status> current() {
            return EnumSet.of(Approved, Active, Completed);
        }

        public static EnumSet<Status> operative() {
            return EnumSet.of(Active, Completed);
        }

        public static EnumSet<Status> former() {
            return EnumSet.of(Cancelled, Closed);
        }

        public static EnumSet<Status> noAutoPay() {
            EnumSet<Status> result = EnumSet.of(Completed, Cancelled, Closed);
            result.addAll(draft()); // disable, currently, draft leases auto pay. 
            return result;
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

        public boolean isOperative() {
            return operative().contains(this);
        }

        public boolean isFormer() {
            return former().contains(this);
        }

        public boolean isNoAutoPay() {
            return noAutoPay().contains(this);
        }

        /**
         * Unit was not selected yet.
         */
        public static boolean isApplicationWithoutUnit(Lease lease) {
            return (lease.status().getValue() == Application) && lease.unit().isNull();
        }

    }

    @I18n(context = "Lease Completion Type")
    @XmlType(name = "LeaseCompletionType")
    public enum CompletionType {

        /*
         * Notice can be given for the Fixed term lease (i.e. Legal Vacate)
         */
        Notice,

        Skip,

        Eviction,

        Termination;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // Data: ----------------------------------------------

    @NotNull
    @ToString(index = 0)
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true, group = { "c,2" })
    @MemberColumn(notNull = true, sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> leaseId();

    //as of now @see PmcYardiCredential or -1 for internal
    @ReadOnly
    @Indexed(group = { "c,2" })
    @MemberColumn(notNull = true)
    IPrimitive<Key> integrationSystemId();

    @NotNull
    @ReadOnly
    @ToString(index = 1)
    @MemberColumn(name = "leaseType")
    IPrimitive<ARCode.Type> type();

    @NotNull
    @ToString(index = 2)
    @Indexed
    AptUnit unit();

    /**
     * lease begin/end:
     * Note: the dates have being re-calculated in {@link LeaseFacade#updateLeaseDates(Lease lease)} method
     */
    @NotNull
    IPrimitive<LogicalDate> leaseFrom();

    @NotNull
    IPrimitive<LogicalDate> leaseTo();

    /**
     * equals to null for active lease
     * set to custom date in case of termination
     */
    IPrimitive<LogicalDate> terminationLeaseTo();

    IPrimitive<LogicalDate> expectedMoveIn();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<LogicalDate> actualMoveIn();

    IPrimitive<LogicalDate> actualMoveOut();

    IPrimitive<LogicalDate> moveOutSubmissionDate();

    @ReadOnly
    @JoinColumn
    @Indexed
    @MemberColumn(notNull = true)
    @Owned(forceCreation = true, cascade = {})
    BillingAccount billingAccount();

    // internals:

    @ToString(index = 3)
    IPrimitive<Status> status();

    IPrimitive<CompletionType> completion();

    @ReadOnly
    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> creationDate();

    // Used for versioning retrieval of approved Application
    IPrimitive<Date> approvalDate();

    IPrimitive<LogicalDate> activationDate();

    @Timestamp(Update.Updated)
    IPrimitive<Date> updated();

    @Owned
    LeaseApplication leaseApplication();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    IList<LeaseTerm> leaseTerms();

    LeaseTerm previousTerm();

    LeaseTerm currentTerm();

    LeaseTerm nextTerm();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<LeaseParticipant<?>> leaseParticipants();

    // Denormalized filed to be able to build query
    @Detached(level = AttachLevel.IdOnly)
    Tenant _applicant();
}
