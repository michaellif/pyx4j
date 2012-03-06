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
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.media.ApplicationDocumentHolder;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocument;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.misc.EquifaxApproval;

@DiscriminatorValue("PersonScreening")
public interface PersonScreening extends IEntity, ApplicationDocumentHolder {

    @Owner
    @Detached
    @ReadOnly
    @JoinColumn
    PersonScreeningHolder screene();

    @Format("MM/dd/yyyy")
    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    @Format("MM/dd/yyyy")
    @Timestamp(Update.Updated)
    IPrimitive<LogicalDate> updateDate();

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

    @Override
    @Owned
    @Detached
    @Caption(name = "Identification Documents")
    @OrderBy(ApplicationDocument.OrderColumnId.class)
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

    @Detached
    @Length(2)
    @JoinTable(value = PersonGuarantor.class, cascade = false)
    @OrderBy(PersonGuarantor.OrderInGuarantee.class)
    IList<PersonGuarantor> guarantors();

    //=============== Security Info =============//

    @NotNull
    IList<IdentificationDocument> identificationDocuments();

    @Deprecated
    IPrimitive<String> driversLicense();

    @Deprecated
    @Caption(name = "Province/State", description = "Province/State, In Which The License Has Been Issued")
    @Editor(type = EditorType.combo)
    Province driversLicenseState();

    @Deprecated
    @Caption(name = "SIN")
    IPrimitive<String> secureIdentifier();

    @Deprecated
    @Caption(name = "Not resident of Canada")
    IPrimitive<Boolean> notCanadianCitizen();

    //=============== Approval =============//

    EquifaxApproval equifaxApproval();
}
