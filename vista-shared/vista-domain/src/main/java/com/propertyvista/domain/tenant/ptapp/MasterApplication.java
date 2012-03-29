/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.ptapp;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.EquifaxApproval;

public interface MasterApplication extends IEntity {

    @I18n(context = "Application")
    @XmlType(name = "ApplicationStatus")
    public enum Status {

        Created, // Mapped to Lease status Created

        Invited, // Mapped to Lease status ApplicationInProgress

        Incomplete, // Mapped to Lease status ApplicationInProgress

        Submitted, // Mapped to Lease status ApplicationInProgress for Application or PendingDecision for MasterApplication

        PendingDecision, // Mapped to Lease status PendingDecision

        InformationRequested, // Mapped to Lease status ApplicationInProgress

        Approved, // Mapped to Lease status Approved

        Declined, // Mapped to Lease status Approved

        Cancelled; // Mapped to Lease status ApplicationCancelled

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ReadOnly
    @ToString
    IPrimitive<String> applicationID();

    IPrimitive<Status> status();

    @Owned
    @Detached
    ISet<Application> applications();

    Employee decidedBy();

    IPrimitive<LogicalDate> decisionDate();

    IPrimitive<String> decisionReason();

    Notes notes();

    @Detached
    @Versioned
    Lease lease();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    EquifaxApproval equifaxApproval();
}
