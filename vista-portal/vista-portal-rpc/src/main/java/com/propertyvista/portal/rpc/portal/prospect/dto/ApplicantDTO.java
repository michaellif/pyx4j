/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestions;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;

public interface ApplicantDTO {

    Person person();

    PriorAddress currentAddress();

    IList<EmergencyContact> emergencyContacts();

    PriorAddress previousAddress();

    @Owned
    @Caption(name = "General Questions")
    CustomerScreeningLegalQuestions legalQuestions();

    //=============== Financial =============//

    @Owned
    @Detached
    @Length(3)
    @Caption(name = "Income")
    IList<CustomerScreeningIncome> incomes();

    @Owned
    @Detached
    @Length(3)
    IList<CustomerScreeningPersonalAsset> assets();

}
