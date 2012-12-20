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

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.AccountType;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface OnlinePaymentSetupDTO extends IEntity {

    @I18n
    enum CompanyType {
        blabla, blablabla;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // Pricing:

// TODO : ask Slava how to get it here from vista-admin-domain!?
//    PmcPaymentTypeInfo paymentType();

    IPrimitive<String> legal();

    // Business Info:
    IPrimitive<CompanyType> companyType();

    IPrimitive<String> companyName();

    AddressSimple companyAddress();

    IPrimitive<String> businessNumber();

    IPrimitive<LogicalDate> establishingDate();

    // Personal Info:

    // Banking Info:
    interface PropertyAccountInfo extends IEntity {

        @NotNull
        Building property();

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

}
