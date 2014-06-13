/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.dto.PaymentDataDTO;

@Transient
public interface PaymentDTO extends PaymentDataDTO, PaymentRecord {

    @Owned
    @Detached
    @Caption(name = "I agree to the service fee being charged and have read the applicable terms and conditions")
    CustomerSignature convenienceFeeSignature();

    IPrimitive<Boolean> completed();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.moneylabel)
    IPrimitive<BigDecimal> applicationFee();
}
