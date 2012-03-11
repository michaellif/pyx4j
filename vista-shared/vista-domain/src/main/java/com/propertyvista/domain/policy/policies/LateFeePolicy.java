/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.framework.BuildingPolicy;

@DiscriminatorValue("LateFeePolicy")
public interface LateFeePolicy extends BuildingPolicy {

    @XmlType(name = "BaseFeeType")
    public enum BaseFeeType {
        @Translate("% Owed-total")
        PercentOwedTotal,

        @Translate("% Monthly Rent")
        PercentMonthlyRent,

        FlatAmount,

        @Translate("% Owed-month")
        PrecentOwedMonth;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @XmlType(name = "BaseFeeType")
    public enum MaxTotalFeeType {
        @Translate("% Owed-total")
        PercentOwedTotal,

        @Translate("% Monthly Rent")
        PercentMonthlyRent,

        FlatAmount,

        @Translate("% Owed-month")
        PrecentOwedMonth;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<BigDecimal> baseFee();

    @NotNull
    IPrimitive<BaseFeeType> baseFeeType();

    @NotNull
    IPrimitive<Integer> gracePeriod();

    IPrimitive<BigDecimal> baseFee2();

    IPrimitive<BaseFeeType> baseFeeType2();

    IPrimitive<Integer> gracePeriod2();

    @NotNull
    IPrimitive<BigDecimal> maxTotalFee();

    @NotNull
    IPrimitive<MaxTotalFeeType> maxTotalFeeType();

    @NotNull
    IPrimitive<BigDecimal> dailyFee();

    @NotNull
    IPrimitive<Integer> maxDays();

    @NotNull
    IPrimitive<BigDecimal> minimumAmounDue();

    @NotNull
    IPrimitive<Boolean> chargeNoticeResident();

    @NotNull
    IPrimitive<Boolean> chargePastResident();

    @NotNull
    @Owned
    @Length(2845)
    @Editor(type = Editor.EditorType.richtextarea)
    //TODO Blob
    IPrimitive<String> lateFeeStatement();
}
