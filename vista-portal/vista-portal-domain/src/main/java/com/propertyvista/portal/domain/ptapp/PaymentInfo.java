/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-23
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.ptapp;

import com.propertyvista.portal.domain.payment.BillingAddress;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.InteracInfo;
import com.propertyvista.portal.domain.payment.PaymentType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface PaymentInfo extends IEntity, IBoundToApplication {

    @Transient
    ChargeLineList applicationCharges();

    @Transient
    ChargeLine applicationFee();

    @Caption(name = "Payment types")
    @Editor(type = EditorType.radiogroup)
    @MemberColumn(name = "tp")
    IPrimitive<PaymentType> type();

    @EmbeddedEntity
    EcheckInfo echeck();

    @EmbeddedEntity
    @Transient
    CreditCardInfo creditCard();

    InteracInfo interac();

    @Caption(name = "Yes plase enrol me")
    IPrimitive<Boolean> preauthorised();

    IPrimitive<Boolean> sameAsCurrent();

    Address currentAddress();

    IPrimitive<String> currentPhone();

    @EmbeddedEntity
    BillingAddress billingAddress();

}
