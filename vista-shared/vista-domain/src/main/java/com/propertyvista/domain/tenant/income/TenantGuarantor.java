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
package com.propertyvista.domain.tenant.income;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IUserEntity;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.media.ApplicationDocumentHolder;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.PersonRelationship;

@ToStringFormat("{0}, {1}")
@DiscriminatorValue("TenantGuarantor")
@Deprecated
public interface TenantGuarantor extends IUserEntity, Person, ApplicationDocumentHolder {

    @ToString(index = 10)
    @NotNull
    IPrimitive<PersonRelationship> relationship();

    @Override
    @Caption(name = "Birth Date")
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> birthDate();

    @Override
    @Owned
    @Caption(name = "Identification Documents")
    @JoinTable(value = ApplicationDocument.class, orderColumn = ApplicationDocument.OrderColumnId.class, cascade = false)
    IList<ApplicationDocument> documents();

// Financial:
    @Owned
    @Length(3)
    @Caption(name = "Income")
    IList<PersonalIncome> incomes();

    @Owned
    @Length(3)
    IList<PersonalAsset> assets();
}
