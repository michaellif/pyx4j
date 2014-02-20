/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.misc.VistaTODO;

@ToStringFormat("{0}, {1}")
public interface ARCode extends IEntity {

    @I18n
    @XmlType(name = "LeaseAdjustmentActionType")
    enum ActionType {
        Debit, Credit;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "ARCodeType")
    enum Type {

        Residential(ActionType.Debit),

        ResidentialShortTerm(ActionType.Debit),

        Commercial(ActionType.Debit),

        Parking(ActionType.Debit),

        Locker(ActionType.Debit),

        Pet(ActionType.Debit),

        Utility(ActionType.Debit),

        AddOn(ActionType.Debit),

        OneTime(ActionType.Debit),

        DepositRefund(ActionType.Credit),

        Deposit(ActionType.Debit),

        AccountCredit(ActionType.Credit),

        AccountCharge(ActionType.Debit),

        CarryForwardCredit(ActionType.Credit),

        CarryForwardCharge(ActionType.Debit),

        NSF(ActionType.Debit),

        LatePayment(ActionType.Debit),

        ExternalCredit(ActionType.Credit),

        ExternalCharge(ActionType.Debit),

        Payment(ActionType.Credit);

        // -----------------------------

        private final ActionType actionType;

        Type(ActionType actionType) {
            this.actionType = actionType;
        }

        public ActionType getActionType() {
            return actionType;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static EnumSet<Type> services() {
            if (VistaTODO.removedForProduction) {
                return EnumSet.of(Residential);
            } else {
                return EnumSet.of(Residential, ResidentialShortTerm, Commercial);
            }
        }

        public static EnumSet<Type> unitRelatedServices() {
            if (VistaTODO.removedForProduction) {
                return EnumSet.of(Residential);
            } else {
                return EnumSet.of(Residential, ResidentialShortTerm, Commercial);
            }
        }

        public static EnumSet<Type> features() {
            return EnumSet.of(Parking, Locker, Pet, Utility, AddOn, OneTime);
        }

        public static EnumSet<Type> deposits() {
            return EnumSet.of(Deposit);
        }

        public static EnumSet<Type> leaseAjustments() {
            return EnumSet.of(AccountCredit, AccountCharge);
        }

        public static Collection<Type> nonMandatoryFeatures() {
            return EnumSet.of(Pet);
        }

        public static Collection<Type> nonReccuringFeatures() {
            return EnumSet.of(OneTime);
        }
    }

    @ToString(index = 0)
    @MemberColumn(name = "codeType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    @Length(50)
    IPrimitive<String> name();

    GlCode glCode();

    @Timestamp
    IPrimitive<Date> updated();

    @NotNull
    IPrimitive<Boolean> reserved();

    /** Named "charge code", however it's applied to both charges and credits */
    @Owned
    @OrderBy(PrimaryKey.class)
    IList<YardiChargeCode> yardiChargeCodes();
}
