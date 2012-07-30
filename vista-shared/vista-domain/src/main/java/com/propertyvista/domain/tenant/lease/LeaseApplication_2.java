/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 5, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.util.Collection;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication_2;
import com.propertyvista.misc.EquifaxApproval;

public interface LeaseApplication_2 extends IEntity {

    @I18n(context = "Lease Application")
    @XmlType(name = "LeaseApplicationStatus")
    public enum Status {

        Created, // Mapped to Lease status Created and ApplicationInProgress

        OnlineApplication,

        PendingDecision,

        Approved, // Mapped to Lease status Approved

        Declined, // Mapped to Lease status Closed

        Cancelled; // Mapped to Lease status Cancelled

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        // state sets:

        public static Collection<Status> draft() {
            return EnumSet.of(Created, OnlineApplication, PendingDecision);
        }

        public static Collection<Status> current() {
            return EnumSet.of(Approved);
        }

        public static Collection<Status> former() {
            return EnumSet.of(Approved, Declined, Cancelled);
        }

        // states:

        public boolean isDraft() {
            return draft().contains(this);
        }

        public boolean isCurrent() {
            return current().contains(this);
        }

        public boolean isFormer() {
            return former().contains(this);
        }
    }

    @Owner
    @JoinColumn
    Lease_2 lease();

    @Detached
    @Versioned
    Lease_2 leaseOnApplication();

    @Owned(cascade = {})
    @Detached
    MasterOnlineApplication_2 onlineApplication();

    @ToString(index = 0)
    IPrimitive<Status> status();

    Employee decidedBy();

    IPrimitive<LogicalDate> decisionDate();

    IPrimitive<String> decisionReason();

    Notes notes();

    EquifaxApproval equifaxApproval();
}
