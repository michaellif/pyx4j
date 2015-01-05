/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2015
 * @author stanp
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@SecurityEnabled
public interface N4LeaseCandidateDTO extends IEntity {

    Lease leaseId();

    IPrimitive<String> propertyCode();

    IPrimitive<String> unitNo();

    IPrimitive<LogicalDate> moveIn();

    IPrimitive<LogicalDate> moveOut();

    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> amountOwed();

    IPrimitive<LogicalDate> lastNotice();
}
