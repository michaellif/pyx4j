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
package com.propertyvista.dto.vista2pmc;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.AccountType;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface OnlinePaymentSetupDTO extends IEntity {

    // Pricing:

// TODO : ask Slava how to get it here from vista-admin-domain!?
//    PmcPaymentTypeInfo paymentType();

    IPrimitive<String> legal();

    // Business Info:
    BusinessInformationDTO businessInformation();

    // Personal Info:
    PersonalInformationDTO personalInformation();

    // Banking Info:
    interface PropertyAccountInfo extends IEntity {

        @NotNull
        Building property();

        @Editor(type = EditorType.money)
        IPrimitive<BigDecimal> averageMonthlyRent();

        IPrimitive<Integer> numberOfRentedUnits();

        @NotNull
        IPrimitive<String> bankName();

        @NotNull
        IPrimitive<AccountType> accountType();

        @NotNull
        IPrimitive<String> transitNumber();

        @NotNull
        IPrimitive<String> institutionNumber();

        @NotNull
        IPrimitive<String> accountNumber();
    }

    IList<PropertyAccountInfo> propertyAccounts();

    // Confirmation:
    AgreementDTO caledonAgreement();

    AgreementDTO caledonSoleProprietorshipAgreement();

    AgreementDTO paymentPadAgreement();

}
