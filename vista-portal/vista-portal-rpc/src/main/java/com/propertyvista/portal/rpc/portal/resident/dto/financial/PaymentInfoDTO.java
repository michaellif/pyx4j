/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 15, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
public interface PaymentInfoDTO extends IEntity {

    @ToString(index = 0)
    @Editor(type = EditorType.moneylabel)
    IPrimitive<BigDecimal> amount();

    @ToString(index = 1)
    @Editor(type = EditorType.label)
    PaymentMethod paymentMethod();

    @ToString(index = 2)
    @Editor(type = EditorType.label)
    Tenant payer();

    @ToString(index = 3)
    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> paymentDate();
}