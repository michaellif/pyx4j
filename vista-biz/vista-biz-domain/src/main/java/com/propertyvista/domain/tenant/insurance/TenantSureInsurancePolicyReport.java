/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface TenantSureInsurancePolicyReport extends IEntity {

    public enum ReportedStatus {

        New,

        Active,

        Cancelled;

    }

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    TenantSureInsurancePolicy insurance();

    IPrimitive<ReportedStatus> reportedStatus();

    /** Denotes the date when reportedStatus has become active */
    IPrimitive<LogicalDate> statusFrom();
}
