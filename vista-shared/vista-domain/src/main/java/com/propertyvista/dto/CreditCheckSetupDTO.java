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

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.info.BusinessInformation;
import com.propertyvista.domain.pmc.info.PersonalInformation;

@Transient
public interface CreditCheckSetupDTO extends IEntity {

    @NotNull
    IPrimitive<CreditCheckReportType> creditPricingOption();

    // BUSINESS INFORMATION SECTION--------------------------------------------

    BusinessInformation businessInformation();

    // PERSONAL INFORMATION SECTION -------------------------------------------
    PersonalInformation personalInformation();

    // CONFIRMATION SECTION ---------------------------------------------------
    CreditCardInfo creditCardInfo();

}
