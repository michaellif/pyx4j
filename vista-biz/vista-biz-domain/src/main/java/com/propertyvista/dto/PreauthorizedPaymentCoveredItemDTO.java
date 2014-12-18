/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-15
 * @author VladL
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.AutopayAgreement;

@Transient
@ToStringFormat("{0}, {1,number,percent}")
public interface PreauthorizedPaymentCoveredItemDTO extends AutopayAgreement.AutopayAgreementCoveredItem {

    @ReadOnly
    @ToString(index = 0)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> covered();

    @ToString(index = 1)
    @Format("#,##00")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percent();
}