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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.contact.IAddressFull;
import com.propertyvista.domain.financial.Money;

@AbstractEntity
@Inheritance
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IEmploymentInfo extends IAddressFull, IIncomeInfo {

    @Override
    @Caption(name = "Employer Name")
    @NotNull
    IPrimitive<String> name();

    //TODO: either one of starts/ends,  may be optional/hidden ?
    @Caption(name = "Employed for (years)")
    IPrimitive<Double> employedForYears();

    @Caption(name = "Supervisor/Manager Name")
    IPrimitive<String> supervisorName();

    @Caption(name = "Supervisor/Manager Phone")
    @Editor(type = EditorType.phone)
    IPrimitive<String> supervisorPhone();

    @Caption(name = "Position")
    IPrimitive<String> position();

    @Override
    @Caption(name = "Monthly Salary")
    @NotNull
    Money monthlyAmount();
}