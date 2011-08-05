/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-06
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.ApplicationDocument;
import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.tenant.income.PersonalIncome;

public interface TenantScreening extends IEntity, TenantScreeningSecureInfoFragment {

    @Owner
    @Detached
    Tenant tenant();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> screeningDate();

    @Owned
    PriorAddress currentAddress();

    @Owned
    PriorAddress previousAddress();

    @Owned
    @Caption(name = "General Questions")
    LegalQuestions legalQuestions();

    @Owned
    IList<ApplicationDocument> documents();

// Financial:
    @Owned
    @Length(3)
    IList<PersonalIncome> incomes();

    @Owned
    @Length(3)
    IList<PersonalAsset> assets();

    @Owned
    @Length(2)
    IList<TenantGuarantor> guarantors();
}
