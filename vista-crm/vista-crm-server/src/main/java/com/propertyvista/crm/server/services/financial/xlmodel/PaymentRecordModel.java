/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial.xlmodel;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;

public interface PaymentRecordModel extends IEntity {

    IPrimitive<String> propertyCode();

    IPrimitive<String> leaseId();

    @Caption(name = "Tenant Id")
    IPrimitive<String> participantId();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @Caption(name = "Payment Type")
    IPrimitive<PaymentType> type();

    IPrimitive<LogicalDate> receivedDate();

    @Editor(type = EditorType.label)
    IPrimitive<PaymentStatus> paymentStatus();
}
