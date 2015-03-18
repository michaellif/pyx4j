/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 */
package com.propertyvista.domain.tenant.prospect;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

/**
 * This is an application progress for tenant, secondary tenant and guarantors.
 */
public interface OnlineApplication extends IEntity {

    @I18n(context = "OnlineApplication")
    @XmlType(name = "OnlineApplicationStatus")
    public enum Status {

        Invited,

        Incomplete,

        InformationRequested,

        Submitted,

        Cancelled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    MasterOnlineApplication masterOnlineApplication();

    @NotNull
    @ReadOnly
    Customer customer();

    @NotNull
    @ReadOnly
    @MemberColumn(name = "participantRole")
    IPrimitive<LeaseTermParticipant.Role> role();

    @Owned
    IList<SignedOnlineApplicationLegalTerm> legalTerms();

    @Owned
    IList<SignedOnlineApplicationConfirmationTerm> confirmationTerms();

    IPrimitive<Status> status();

    @Owned
    ISet<OnlineApplicationWizardStepStatus> stepsStatuses();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    @ReadOnly
    IPrimitive<LogicalDate> submitDate();
}
