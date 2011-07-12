/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.domain.payment.BillingAddress;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.InteracInfo;
import com.propertyvista.portal.domain.payment.PaymentType;

@Transient
public interface PaymentMethodGenericDTO extends IEntity {

    @Caption(name = "Payment types")
    @Editor(type = EditorType.radiogroup)
    IPrimitive<PaymentType> type();

    EcheckInfo echeck();

    CreditCardInfo creditCard();

    InteracInfo interac();

    BillingAddress billingAddress();

    IPrimitive<Boolean> primary();

}
