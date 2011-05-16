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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.common.domain.IAddressFull;
import com.propertyvista.common.domain.financial.Money;

public interface IncomeInfoStudentIncome extends IEntity, IAddressFull, IIncomeInfo {

    public enum FundingChoice {
        scolarship, bursary, grant, loan
    }

    public enum Program {

        @Translation("Undergraduate")
        undergraduate,

        @Translation("Graduate")
        graduate;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Override
    @Caption(name = "School")
    IPrimitive<String> name();

    @Override
    @Caption(name = "Gross monthly amount")
    @NotNull
    Money monthlyAmount();

    @Override
    @Caption(name = "Program Start")
    IPrimitive<java.sql.Date> starts();

    @Override
    @Caption(name = "Program (Planned) to be completed on")
    IPrimitive<java.sql.Date> ends();

    @Caption(name = "Program")
    IPrimitive<Program> program();

    IPrimitive<String> fieldOfStudy();

    IPrimitive<FundingChoice> fundingChoices();

}
