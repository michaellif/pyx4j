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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.tenant.lease.Deposit;

@ToStringFormat("{2}, {1,choice,Percentage#{0,number,percent}|Amount#${0,number,#.##}}, {3}")
public interface DepositPolicyItem extends IEntity {

    @I18n
    @XmlType(name = "ValueType")
    public enum ValueType {
        Monetary, Percentage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    DepositPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

    /**
     * Could be null for security deposits
     */
    ProductItemType productType();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "depositValue")
    IPrimitive<BigDecimal> value();

    @NotNull
    @ToString(index = 1)
    IPrimitive<ValueType> valueType();

    @NotNull
    @Length(40)
    @ToString(index = 2)
    IPrimitive<String> description();

    @NotNull
    @ToString(index = 3)
    IPrimitive<Deposit.DepositType> depositType();

    @NotNull
    IPrimitive<BigDecimal> annualInterestRate();

    IPrimitive<Integer> securityDepositRefundWindow();
}
