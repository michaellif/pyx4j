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
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface IncomeInfoStudentIncome extends IEntity, IAddress, IIncomeInfo {

    public enum FundingChoice {
        scolarship, bursary, grant, loan
    }

    public enum Program {

        //TODO i18n
        undergraduate("Undergraduate"),

        graduate("Graduate");

        private final String label;

        Program() {
            this.label = name();
        }

        Program(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
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
    IPrimitive<Date> starts();

    @Override
    @Caption(name = "Program (Planned) to be completed on")
    IPrimitive<Date> ends();

    @Caption(name = "Program")
    IPrimitive<Program> program();

    IPrimitive<String> fieldOfStudy();

    IPrimitive<FundingChoice> fundingChoices();

}
