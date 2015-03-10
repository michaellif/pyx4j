/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2015
 * @author vlads
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.AutopayAgreement;

@Transient
@ExtendsBO
public interface AutoPayReconciliationDTO extends AutopayAgreement {

    IPrimitive<LogicalDate> renewalDate();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> rentCharge();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> parkingCharges();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> otherCharges();

    @Format("#,##0.00")
    @Caption(name = "Price total")
    IPrimitive<BigDecimal> price();

    @Format("#,##0.00")
    @Caption(name = "Payment total")
    IPrimitive<BigDecimal> payment();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> paymentShareAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> discrepancy();

    IPrimitive<Integer> count();

    IPrimitive<String> notice();

}
