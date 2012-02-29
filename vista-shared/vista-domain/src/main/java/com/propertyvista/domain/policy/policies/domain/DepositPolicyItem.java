/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.ProductItemType;

@ToStringFormat("{2}, {1}, {0}, {3}, {4}")
public interface DepositPolicyItem extends IEntity {

    @I18n
    @XmlType(name = "ApplyToRepayAt")
    public enum ApplyToRepayAt {
        leaseEnd, firstMonth, lastMonth;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "RepaymentMode")
    public enum RepaymentMode {
        @Translate("Apply")
        apply,

        @Translate("Return")
        returned;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "ValueType")
    public enum ValueType {
        amount,

        percentage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "depositValue")
    IPrimitive<BigDecimal> value();

    @NotNull
    @ToString(index = 1)
    IPrimitive<ValueType> valueType();

    @NotNull
    @Length(20)
    @ToString(index = 2)
    IPrimitive<String> name();

    @NotNull
    @ToString(index = 3)
    IPrimitive<RepaymentMode> repaymentMode();

    @NotNull
    @Caption(name = "Apply to/Re-payed at")
    @ToString(index = 4)
    IPrimitive<ApplyToRepayAt> applyToRepayAt();

    /**
     * Could be null for security deposits
     */
    ProductItemType appliedTo();
}
