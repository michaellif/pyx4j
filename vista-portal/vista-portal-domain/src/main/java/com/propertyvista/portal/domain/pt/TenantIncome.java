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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface TenantIncome extends IEntity {

    @Deprecated
    public static enum IncomeType {
        pension, unemployment, retired, odsp, dividends, other
    }

    @Caption(name = "Description")
    @Deprecated
    IPrimitive<IncomeType> type();

    IPrimitive<IncomeSource> incomeSource();

    @Caption(name = "Monthly amount")
    IPrimitive<Double> monthlyAmount();

    // incomeSource = other 
    //TODO description    

    // incomeSource =  pension, retired, odsp, dividends
    // NOTHING

    // incomeSource = fulltime, parttime
    @Owned
    Employer employer();

    // incomeSource = selfemployed
    @Owned
    SelfEmployed selfEmployed();

    // incomeSource = seasonallyEmployed
    @Owned
    SeasonallyEmployed seasonallyEmployed();

    // incomeSource = socialServices
    @Owned
    SocialServices socialServices();

    // incomeSource = student
    @Owned
    StudentIncome studentIncome();

    // incomeSource =  unemployment
    // TODO add "Stop On"
}
