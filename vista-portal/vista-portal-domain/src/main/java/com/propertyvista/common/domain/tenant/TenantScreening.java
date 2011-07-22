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
package com.propertyvista.common.domain.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.ApplicationDocument;
import com.propertyvista.portal.domain.ptapp.LegalQuestions;
import com.propertyvista.portal.domain.ptapp.TenantAsset;
import com.propertyvista.portal.domain.ptapp.TenantGuarantor;
import com.propertyvista.portal.domain.ptapp.TenantIncome;

public interface TenantScreening extends IEntity, TenantScreeningSecureInfoFragment {

    @Detached
    Tenant tenant();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> screeningData();

    @Owned
    Address currentAddress();

    @Owned
    Address previousAddress();

    @Owned
    @Caption(name = "General Questions")
    LegalQuestions legalQuestions();

    @Owned
    IList<ApplicationDocument> documents();

    @Owned
    @Length(3)
    IList<TenantIncome> incomes();

    @Owned
    @Length(3)
    IList<TenantAsset> assets();

    @Owned
    @Length(2)
    IList<TenantGuarantor> guarantors();
}
