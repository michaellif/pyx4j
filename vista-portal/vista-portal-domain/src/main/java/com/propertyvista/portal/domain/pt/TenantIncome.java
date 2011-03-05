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
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface TenantIncome extends IEntity {

    @NotNull
    IPrimitive<IncomeSource> incomeSource();

    // incomeSource =  pension, retired, odsp, dividends
    // NOTHING

    // incomeSource =  unemployment and other
    @Owned
    IncomeInfoOther otherIncomeInfo();

    // incomeSource = fulltime, parttime
    @Owned
    IncomeInfoEmployer employer();

    // incomeSource = selfemployed
    @Owned
    IncomeInfoSelfEmployed selfEmployed();

    // incomeSource = seasonallyEmployed
    @Owned
    IncomeInfoSeasonallyEmployed seasonallyEmployed();

    // incomeSource = socialServices
    @Owned
    IncomeInfoSocialServices socialServices();

    // incomeSource = student
    @Owned
    IncomeInfoStudentIncome studentIncome();

}
