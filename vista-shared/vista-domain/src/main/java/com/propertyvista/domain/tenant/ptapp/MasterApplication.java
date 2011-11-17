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
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.Notes;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;

public interface MasterApplication extends IEntity {

    @I18n
    @XmlType(name = "Suggested Decision")
    public enum Decision {

        Pending,

        Approve,

        @Translate("Request for Guarantor or further information")
        RequestInfo,

        Decline;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owned
    IList<Application> applications();

    @Format("#0.00")
    @Caption(name = "% Rent Approved (based on Equifax check)")
    IPrimitive<Double> percenrtageApproved();

    @Caption(name = "Suggested Decision (based on Equifax check)")
    IPrimitive<Decision> suggestedDecision();

    Employee decidedBy();

    IPrimitive<LogicalDate> decisionDate();

    IPrimitive<String> decisionReason();

    Notes notes();

    @Detached
    Lease lease();

    @ReadOnly
    IPrimitive<LogicalDate> createDate();
}
