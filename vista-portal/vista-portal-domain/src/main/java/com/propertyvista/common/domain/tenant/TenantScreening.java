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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.domain.ptapp.ApplicationDocument;
import com.propertyvista.portal.domain.ptapp.LegalQuestions;
import com.propertyvista.portal.domain.ptapp.TenantAsset;
import com.propertyvista.portal.domain.ptapp.TenantGuarantor;
import com.propertyvista.portal.domain.ptapp.TenantIncome;

public interface TenantScreening extends IEntity {

    @Detached
    Tenant tenant();

    // secure information
    IPrimitive<String> driversLicense();

    @Caption(name = "Province/State", description = "Province/State, in which a license has been issued.")
    @Editor(type = EditorType.combo)
    Province driversLicenseState();

    @Caption(name = "SIN")
    IPrimitive<String> secureIdentifier();

    @Caption(name = "I'm not resident of Canada")
    IPrimitive<Boolean> notCanadianCitizen();

    @Owned
    @Caption(name = "General Questions")
    LegalQuestions legalQuestions();

    @Owned
    IList<ApplicationDocument> documents();

    @Owned
    @Length(3)
    @Detached
    IList<TenantIncome> incomes();

    @Owned
    @Length(3)
    @Detached
    IList<TenantAsset> assets();

    @Owned
    @Length(2)
    @Detached
    IList<TenantGuarantor> guarantors();
}
