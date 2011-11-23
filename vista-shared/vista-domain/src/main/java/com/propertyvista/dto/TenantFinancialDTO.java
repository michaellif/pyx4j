/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;

@Transient
public interface TenantFinancialDTO extends IEntity {

    @ToString
    Person person();

    @Caption(name = "Income")
    IList<PersonalIncome> incomes();

    @Caption(name = "Income (Other)")
    IList<IIncomeInfo> incomes2();

    IList<PersonalAsset> assets();

    @Caption(name = "Guarantor(s)")
    IList<TenantGuarantor> guarantors();
}
