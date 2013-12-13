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
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestions;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.lead.Lead.RefSource;

@Transient
public interface ApplicantDTO extends IEntity {

    Person person();

    CustomerPicture picture();

    IList<IdentificationDocumentFolder> documents();

    PriorAddress currentAddress();

    PriorAddress previousAddress();

    IList<EmergencyContact> emergencyContacts();

    @Owned
    @Caption(name = "General Questions")
    CustomerScreeningLegalQuestions legalQuestions();

    @Caption(name = "How did you hear about us")
    IPrimitive<RefSource> refSource();

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
