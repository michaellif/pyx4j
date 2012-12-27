/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.dto;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.person.Name;

@Transient
public interface CreditCheckSetupDTO extends IEntity {

    @I18n(context = "Credit Report Option")
    @XmlType(name = "CreditReportOption")
    public enum CreditReportOption {

        ReccomendationReport,

        FullCreditReport;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };

    }

    @I18n(context = "Company Type")
    @XmlType(name = "CompanyType")
    public enum CompanyType {

        SoleProprietorship, Partnership, Corporation, Cooperative;

    }

    // PRICING SECTION --------------------------------------------------------

    @NotNull
    IPrimitive<CreditReportOption> creditReportOption();

    // BUSINESS INFORMATION SECTION--------------------------------------------

    // TODO not sure that this is actually what was the poet's intention in the specs...
    // TODO also I'm suspecting that the set acceptable values can change based on the country
    @NotNull
    IPrimitive<CompanyType> companyType();

    @NotNull
    IPrimitive<AddressSimple> businessAddress();

    @Caption(name = "Business Number / Employer Identification")
    IPrimitive<String> businessNumber();

    IPrimitive<LogicalDate> businessEstablishedDate();

    // TODO add buisness proof license upload

    // TODO add Articles of Incorporation upload

    // PERSONAL INFORMATION SECTION -------------------------------------------

    @NotNull
    IPrimitive<Name> name();

    IPrimitive<AddressSimple> personalAddress();

    @NotNull
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    IPrimitive<LogicalDate> dateOfBirth();

    IPrimitive<String> sin();

    // TODO add id doc upload

    // CONFIRMATION SECTION ---------------------------------------------------

    IPrimitive<CreditCardInfo> creditCardInfo();
}
