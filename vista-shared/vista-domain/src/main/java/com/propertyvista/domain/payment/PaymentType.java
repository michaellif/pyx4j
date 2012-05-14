/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import java.util.EnumSet;

import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

public enum PaymentType {

    Cash,

    // TODO move to en_CA translation
    @Translate("Cheque")
    Check,

    @Translate("eCheque")
    Echeck,

    EFT,

    CreditCard,

    @Deprecated
    Visa,

    @Translate("MasterCard")
    @Deprecated
    MasterCard,

    @Deprecated
    Discover,

    Interac;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

    // group sets:

    public static EnumSet<PaymentType> avalableInPortal() {
        return EnumSet.of(Echeck, CreditCard, Interac);
    }

    public static EnumSet<PaymentType> avalableInCrm() {
        return EnumSet.of(Cash, Check, Echeck, CreditCard);
    }

    public static EnumSet<PaymentType> avalableInProfile() {
        return EnumSet.of(Echeck, CreditCard);
    }

    // grouping:

    public boolean isTransactable() {
        return EnumSet.of(Echeck, EFT, CreditCard).contains(this);
    }
}
