/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.contact.AddressSimple;

@AbstractEntity
@Inheritance
@ToStringFormat("${0}, {1}, {2}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IEmploymentInfo extends CustomerScreeningIncomeInfo {

    @NotNull
    @Override
    @ToString(index = 1)
    @Caption(name = "Employer Name")
    IPrimitive<String> name();

    @EmbeddedEntity
    AddressSimple address();

    //TODO: either one of starts/ends,  may be optional/hidden ?
    @Caption(name = "Employed for (years)")
    IPrimitive<Double> employedForYears();

    @Caption(name = "Supervisor/Manager Name")
    @NotNull
    IPrimitive<String> supervisorName();

    @Caption(name = "Supervisor/Manager Phone")
    @Editor(type = EditorType.phone)
    @NotNull
    IPrimitive<String> supervisorPhone();

    @NotNull
    @ToString(index = 2)
    @Caption(name = "Position")
    IPrimitive<String> position();

    @NotNull
    @Override
    @ToString(index = 0)
    @Caption(name = "Monthly Salary")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> monthlyAmount();
}