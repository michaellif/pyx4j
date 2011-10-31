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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InteracInfo;
import com.propertyvista.domain.payment.PaymentType;

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
    @Caption(name = "eCheck")
    EcheckInfo echeck();

    @EmbeddedEntity
    @Transient
    CreditCardInfo creditCard();

    InteracInfo interac();

    @Caption(name = "Yes please enroll me")
    IPrimitive<Boolean> preauthorised();

    IPrimitive<Boolean> sameAsCurrent();

    PriorAddress currentAddress();

    @EmbeddedEntity
    Phone currentPhone();

    @EmbeddedEntity
    AddressStructured billingAddress();

    @EmbeddedEntity
    Phone phone();
}
