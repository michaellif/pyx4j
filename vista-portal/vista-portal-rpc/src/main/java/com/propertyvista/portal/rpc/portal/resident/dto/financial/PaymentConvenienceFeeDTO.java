/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.financial;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.AbstractPaymentMethod;

@Transient
public interface PaymentConvenienceFeeDTO extends IEntity {

    @NotNull
    @ToString(index = 0)
    AbstractPaymentMethod paymentMethod();

    @NotNull
    @ToString(index = 1)
    @Format("$#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

}
