/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.dto.PaymentDataDTO;

@Transient
@ToStringFormat("")
@ExtendsBO(AutopayAgreement.class)
public interface AutoPayDTO extends PaymentDataDTO, com.propertyvista.dto.PreauthorizedPaymentDTO {

    @ReadOnly
    @ToString(index = 10)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Payment total amount")
    IPrimitive<BigDecimal> total();

    IPrimitive<LogicalDate> nextPaymentDate();
}
