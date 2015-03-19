/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author michaellif
 */
package com.propertyvista.domain.tenant.prospect;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
public interface OnlineApplicationStatus extends IEntity {

    @Editor(type = EditorType.label)
    IPrimitive<OnlineApplication.Status> status();

    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> submissionDate();

    @Editor(type = EditorType.label)
    Customer customer();

    /**
     * Applicant, Co-Applicant or Guarantor
     */
    @Editor(type = EditorType.label)
    IPrimitive<LeaseTermParticipant.Role> role();

    /**
     * Completed steps/total steps in %
     */
    @Format("#")
    @Editor(type = EditorType.percentagelabel)
    IPrimitive<BigDecimal> progress();

    @Editor(type = EditorType.label)
    IPrimitive<Long> daysOpen();
}
