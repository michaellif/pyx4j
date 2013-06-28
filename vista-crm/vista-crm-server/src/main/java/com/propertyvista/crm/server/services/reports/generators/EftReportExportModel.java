/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface EftReportExportModel extends IEntity {

    IPrimitive<LogicalDate> targetDate();

    IPrimitive<String> building();

    IPrimitive<String> unit();

    IPrimitive<String> leaseId();

    IPrimitive<Lease.Status> leaseStatus();

    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<LogicalDate> expectedMoveOut();

    IPrimitive<String> participantId();

    IPrimitive<String> customer();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> amount();

    IPrimitive<PaymentType> paymentType();

    IPrimitive<String> bankId();

    IPrimitive<String> transitNumber();

    IPrimitive<String> accountNumber();

    IPrimitive<PaymentStatus> paymentStatus();

    IPrimitive<String> notice();
}
