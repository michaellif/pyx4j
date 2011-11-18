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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;

public interface TenantScreening extends IEntity {

    @Owner
    @Detached
    @ReadOnly
    Tenant tenant();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> screeningDate();

    /**
     * TODO I think that it is better to have a list here since some forms may ask for
     * more than one previous address
     */
    @EmbeddedEntity
    PriorAddress currentAddress();

    @EmbeddedEntity
    PriorAddress previousAddress();

    @Owned
    @Caption(name = "General Questions")
    LegalQuestions legalQuestions();

    @Owned
    @Detached
    IList<ApplicationDocument> documents();

    //=============== Financial =============//

    @Owned
    @Detached
    @Length(3)
    @Caption(name = "Income")
    IList<PersonalIncome> incomes();

    @Owned
    @Length(3)
    @Transient
    @Caption(name = "Income (Other)")
    IList<IIncomeInfo> incomes2();

    @Owned
    @Detached
    @Length(3)
    IList<PersonalAsset> assets();

    @Owned
    @Detached
    @Length(2)
    IList<TenantGuarantor> guarantors();

    //=============== Security Info =============//

    IPrimitive<String> driversLicense();

    @Caption(name = "Province/State", description = "Province/State, in which a license has been issued")
    @Editor(type = EditorType.combo)
    Province driversLicenseState();

    @Caption(name = "SIN")
    IPrimitive<String> secureIdentifier();

    @Caption(name = "Not resident of Canada")
    IPrimitive<Boolean> notCanadianCitizen();

}
