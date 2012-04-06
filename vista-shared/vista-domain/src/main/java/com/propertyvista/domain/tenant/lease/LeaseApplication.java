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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.ptapp.OnlineMasterApplication;
import com.propertyvista.misc.EquifaxApproval;

public interface LeaseApplication extends IEntity {

    @I18n(context = "Lease Application")
    @XmlType(name = "LeaseApplicationStatus")
    public enum Status {

        Draft, // Mapped to Lease status Created and ApplicationInProgress

        OnlineApplicationInProgress,

        PendingDecision,

        Approved, // Mapped to Lease status Approved

        Declined, // Mapped to Lease status Closed

        Cancelled; // Mapped to Lease status Closed

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @JoinColumn
    Lease lease();

    @Owned
    OnlineMasterApplication onlineApplication();

    Employee decidedBy();

    IPrimitive<LogicalDate> decisionDate();

    IPrimitive<String> decisionReason();

    Notes notes();

    EquifaxApproval equifaxApproval();
}
