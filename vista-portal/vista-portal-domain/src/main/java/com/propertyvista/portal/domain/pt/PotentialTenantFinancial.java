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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface PotentialTenantFinancial extends IApplicationEntity {

    public static enum EmploymentType {
        none, fulltime, parttime, selfemployed, seasonallyEmployed, socialServices, student
    }

    @Caption(name = "Employment type")
    IPrimitive<EmploymentType> occupation();

    @Owned
    Employer currentEmployer();

    @Owned
    Employer previousEmployer();

    @Owned
    SelfEmployed selfEmployed();

    @Owned
    SeasonallyEmployed seasonallyEmployed();

    @Owned
    SocialServices socialServices();

    @Owned
    StudentIncome studentIncome();

    @Owned
    IList<TenantIncome> incomes();

    @Owned
    IList<TenantAsset> assets();

    @Owned
    IList<TenantGuarantor> guarantors();

}
