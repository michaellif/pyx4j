/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author vlads
 */
package com.propertyvista.domain.tenant.income;

import java.util.EnumSet;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum IncomeSource {

    @Translate("Full Time Employment")
    fulltime,

    @Translate("Part Time Employment")
    parttime,

    @Translate("Self Employed")
    selfemployed,

    seasonallyEmployed,

    socialServices,

    pension,

    retired,

    @Translate("Student")
    student,

    unemployed,

    disabilitySupport,

    dividends,

    other;

    public static EnumSet<IncomeSource> employment() {
        return EnumSet.of(fulltime, parttime, selfemployed, seasonallyEmployed);
    }

    public static EnumSet<IncomeSource> otherIncome() {
        EnumSet<IncomeSource> values = EnumSet.allOf(IncomeSource.class);
        values.removeAll(employment());
        return values;
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
