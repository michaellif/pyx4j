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
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.InternationalAddress;

@ToStringFormat("${0}, {1}")
@DiscriminatorValue("student")
@Caption(name = "Income Information Student Income")
public interface IncomeInfoStudentIncome extends CustomerScreeningIncomeInfo {

    public enum FundingChoice {
        scolarship, bursary, grant, loan
    }

    public enum Program {

        @Translate("Undergraduate")
        undergraduate,

        @Translate("Graduate")
        graduate;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @Override
    @ToString(index = 1)
    @Caption(name = "School")
    IPrimitive<String> name();

    @EmbeddedEntity
    InternationalAddress address();

    @Override
    @NotNull
    @Caption(name = "Program Start")
    IPrimitive<LogicalDate> starts();

    @Override
    @Caption(name = "Program End")
    IPrimitive<LogicalDate> ends();

    @Caption(name = "Program")
    IPrimitive<Program> program();

    IPrimitive<String> fieldOfStudy();

    IPrimitive<FundingChoice> fundingChoices();

}
