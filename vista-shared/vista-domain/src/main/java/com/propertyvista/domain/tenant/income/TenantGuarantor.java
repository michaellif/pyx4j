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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.person.Person;

public interface TenantGuarantor extends IEntity, Person {

    @I18n
    public enum Relationship {

        Mother, Father, Grandfather, Grandmother, Uncle, Aunt, Friend, Other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 0)
    @NotNull
    IPrimitive<Relationship> relationship();

    @Override
    @Caption(name = "Birth Date")
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> birthDate();

    @Owned
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
