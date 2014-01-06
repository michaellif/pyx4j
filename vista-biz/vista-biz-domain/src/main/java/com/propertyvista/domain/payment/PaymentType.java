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

import com.propertyvista.misc.VistaTODO;

public enum PaymentType {

    Cash,

    Check,

    @Translate("eCheck (ACH)")
    Echeck,

    DirectBanking,

    @Translate("Credit/Debit Card")
    CreditCard,

    Interac;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

    // group sets:

    public static EnumSet<PaymentType> availableInPortal() {
        if (VistaTODO.removedForProduction) {
            return EnumSet.of(Echeck, CreditCard);
        } else {
            return EnumSet.of(Echeck, CreditCard, Interac);
        }
    }

    public static EnumSet<PaymentType> availableInCrm() {
        return EnumSet.of(Cash, Check, Echeck, CreditCard);
    }

    public static EnumSet<PaymentType> availableInProfile() {
        return EnumSet.of(Echeck, CreditCard);
    }

    public static EnumSet<PaymentType> availableForPmc() {
        return EnumSet.of(CreditCard);
    }

    public static EnumSet<PaymentType> availableInInsurance() {
        return EnumSet.of(CreditCard);
    }

    public static EnumSet<PaymentType> availableForNsf() {
        return EnumSet.of(Check, Echeck, DirectBanking, CreditCard);
    }

    public static EnumSet<PaymentType> notAllowedInProfile() {
        return EnumSet.of(Cash, Check);
    }

    public static EnumSet<PaymentType> electronicPayments() {
        return EnumSet.of(Echeck, CreditCard);
    }

    public static EnumSet<PaymentType> transactable() {
        return EnumSet.of(Echeck, DirectBanking, CreditCard);
    }

    public static EnumSet<PaymentType> schedulable() {
        return EnumSet.of(Echeck, CreditCard);
    }

    public static EnumSet<PaymentType> receiveDateEditable() {
        return EnumSet.of(Cash, Check);
    }

    // grouping:

    public boolean isTransactable() {
        return transactable().contains(this);
    }

    public boolean isSchedulable() {
        return schedulable().contains(this);
    }

    public boolean isReceiveDateEditable() {
        return receiveDateEditable().contains(this);
    }

}
