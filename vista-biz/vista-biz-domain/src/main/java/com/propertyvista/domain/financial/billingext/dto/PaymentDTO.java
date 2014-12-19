/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2012
 * @author stanp
 */
package com.propertyvista.domain.financial.billingext.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface PaymentDTO extends TransactionDTO {
    IPrimitive<String> paymentType();
}
