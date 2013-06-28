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
package com.propertyvista.domain.payment;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("{0}: {1}, {2}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("CreditCard")
public interface CreditCardInfo extends PaymentDetails {

    public enum CreditCardType {

        Visa("4"),

        @Translate("MasterCard")
        MasterCard("51", "52", "53", "54", "55");

        // TODO Use PmcPaymentTypeInfo
//        Discover("6011", "622126-622925", "644-649", "65");

//      @Translate("American Express")
//      Amex;

        /** pattern of prefix (IIN - Issuer Identification Number), can be either number or range i.e. "55-689" */
        public final String[] iinsPatterns;

        CreditCardType(String... iinsPatterns) {
            this.iinsPatterns = iinsPatterns;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<CreditCardType> cardType();

    @NotNull
    @ToString(index = 1)
    @Caption(name = "Card Number")
    CreditCardNumberIdentity card();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<String> token();

    @NotNull
    @ToString(index = 2)
    @Format("MM/yyyy")
    @Caption(name = "Expiry Date")
    @Editor(type = EditorType.monthyearpicker)
    IPrimitive<LogicalDate> expiryDate();

    @NotNull
    @Caption(name = "Card Security Code")
    @Transient
    @LogTransient
    IPrimitive<String> securityCode();

    @NotNull
    @Caption(name = "Name On Card")
    IPrimitive<String> nameOn();

    IPrimitive<String> bankPhone();
}
