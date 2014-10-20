/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
/** The hyperlink entites have the same name but end with underscore */
public interface EftReportRecordDTO extends IEntity {

    IPrimitive<Boolean> hasComments();

    IPrimitive<String> notice();

    IPrimitive<LogicalDate> billingCycleStartDate();

    IPrimitive<String> leaseId();

    Lease leaseId_();

    IPrimitive<Status> leaseStatus();

    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<String> building();

    Building building_();

    IPrimitive<String> unit();

    AptUnit unit_();

    IPrimitive<String> participantId();

    Customer customer();

    Tenant customer_();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> amount();

    PaymentRecord amount_();

    IPrimitive<PaymentType> paymentType();

    IPrimitive<PaymentRecord.PaymentStatus> paymentStatus();

    IPrimitive<LogicalDate> targetDate();

    IPrimitive<String> bankId();

    IPrimitive<String> transitNumber();

    IPrimitive<String> accountNumber();
}
