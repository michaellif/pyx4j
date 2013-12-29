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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public interface LateFeeItem extends IEntity {

    @XmlType(name = "BaseFeeType")
    public enum BaseFeeType {
        @Translate("% Owed-total")
        PercentOwedTotal,

        @Translate("% Monthly Rent")
        PercentMonthlyRent,

        FlatAmount;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @XmlType(name = "MaxTotalFeeType")
    public enum MaxTotalFeeType {

        @Translate("% Monthly Rent")
        PercentMonthlyRent,

        FlatAmount,

        Unlimited;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    LeaseBillingPolicy policy();

    @NotNull
    @ToString(index = 1)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> baseFee();

    @NotNull
    @ToString(index = 0)
    IPrimitive<BaseFeeType> baseFeeType();

    @NotNull
    @ToString(index = 3)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> maxTotalFee();

    @NotNull
    @ToString(index = 2)
    IPrimitive<MaxTotalFeeType> maxTotalFeeType();
}
