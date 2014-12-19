/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2012
 * @author stanp
 */
package com.propertyvista.domain.financial.billingext.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface PaymentRecordDTO extends IEntity {

    IPrimitive<String> transactionId();

    IPrimitive<String> externalTransactionId();

    IPrimitive<String> leaseId();

    IPrimitive<LogicalDate> transactionDate();

    IPrimitive<BigDecimal> amount();

    IPrimitive<String> paymentType();

}
