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
import com.pyx4j.entity.annotations.MemberColumn;
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

    @I18n
    @XmlType(name = "LeaseStatus")
    public enum Status {

        Draft,

        ApplicationInProgress,

        PendingApproval,

        Declined,

        Approved,

        Completed,

        Broken,

        Transferred;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> leaseID();

    IPrimitive<Status> status();

    @NotNull
    @MemberColumn(name = "leaseType")
    IPrimitive<Service.Type> type();

    @Detached
    // should be loaded in service when necessary!..
    @Caption(name = "Selected Unit")
    AptUnit unit();

    /**
     * Double-references - TenantInLease holds main reference!
     * Note: Is not maintained! Should be synchronised if necessary in service!!!
     * 
     * @link LeaseCrudService re-fills this list every time on Lease retrieve with current tenants which has referenced the Lease.
     *       Every other service retrieved the lease supposed to do the same!!!
     */
    @Detached
    IList<TenantInLease> tenants();

    // Dates:
    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<LogicalDate> expectedMoveIn();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<LogicalDate> actualMoveIn();

    IPrimitive<LogicalDate> actualMoveOut();

    IPrimitive<LogicalDate> moveOutNotice();

    IPrimitive<LogicalDate> signDate();

    @EmbeddedEntity
    ServiceAgreement serviceAgreement();

    @Detached
    // should be loaded in service when necessary!..
    IList<Document> documents();
}
