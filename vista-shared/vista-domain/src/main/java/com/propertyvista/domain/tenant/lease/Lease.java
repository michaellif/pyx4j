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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.media.Document;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;

public interface Lease extends IEntity {

    @I18n(context = "Lease Status")
    @XmlType(name = "LeaseStatus")
    public enum Status {

        Draft,

        ApplicationInProgress,

        PendingDecision,

        Approved, // Application

        Declined, // Application

        ApplicationCancelled, // Application

        Completed,

        Broken,

        Transferred;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> leaseID();

    IPrimitive<Status> status();

    @NotNull
    @MemberColumn(name = "leaseType")
    IPrimitive<Service.Type> type();

    @NotNull
    @Detached
    // should be loaded in service when necessary!..
    @Caption(name = "Selected Unit")
    AptUnit unit();

    @Detached
    @JoinTable(value = TenantInLease.class, orderColumn = TenantInLease.OrderInLeaseId.class, cascade = false)
    IList<TenantInLease> tenants();

    // Dates:
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseFrom();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> leaseTo();

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

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> signDate();

    @EmbeddedEntity
    ServiceAgreement serviceAgreement();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();

    @ReadOnly
    IPrimitive<LogicalDate> createDate();

}
