/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

/**
 * General required information for all Income types.
 */
@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface CustomerScreeningIncomeInfo extends IEntity {

    public enum AmountPeriod {

        Annually,

        @Translate("Semi-Annually")
        SemiAnnually,

        Quaterly, Monthly,

        @Translate("Semi-Monthly")
        SemiMonthly,

        @Translate("Bi-Weekly")
        BiWeekly,

        Weekly, Daily, Hourly;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 1)
    @Caption(name = "Description")
    IPrimitive<String> name();

    @NotNull
    @Format("#,##0.00")
    @ToString(index = 0)
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> incomeAmount();

    @Editor(type = EditorType.label)
    IPrimitive<AmountPeriod> amountPeriod();

    /**
     * Start of income period. For employment that would be employment start date.
     */
    @Caption(name = "Start Date")
    IPrimitive<LogicalDate> starts();

    @Caption(name = "End Date")
    IPrimitive<LogicalDate> ends();
}
