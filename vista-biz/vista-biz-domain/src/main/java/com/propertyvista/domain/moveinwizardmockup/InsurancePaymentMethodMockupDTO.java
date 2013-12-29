/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.moveinwizardmockup;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo;

@Transient
public interface InsurancePaymentMethodMockupDTO extends IEntity {

    @I18n
    public enum PaymentMethod {

        Visa, MasterCard, Amex;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    IPrimitive<PaymentMethod> paymentMethod();

    @Owned
    @Deprecated
    CreditCardInfo creditCard();

    IPrimitive<Boolean> sameAsCurrent();

    @EmbeddedEntity
    AddressSimple billingAddress();

    @EmbeddedEntity
    AddressSimple currentAddress();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

}
