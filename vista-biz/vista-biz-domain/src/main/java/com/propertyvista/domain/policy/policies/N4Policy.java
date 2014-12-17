/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.framework.Policy;

@DiscriminatorValue("N4Policy")
public interface N4Policy extends Policy {

    @I18n
    public enum EmployeeSelectionMethod {
        ByLoggedInUser, FromEmployeeList;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<EmployeeSelectionMethod> agentSelectionMethod();

    @Caption(description = "Signature image taken from Employee's profile")
    IPrimitive<Boolean> includeSignature();

    @NotNull
    IPrimitive<String> companyName();

    @EmbeddedEntity
    InternationalAddress mailingAddress();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phoneNumber();

    @Editor(type = EditorType.phone)
    IPrimitive<String> faxNumber();

    @Editor(type = EditorType.email)
    IPrimitive<String> emailAddress();

    @RpcTransient
    IList<ARCode> relevantARCodes();

    @NotNull
    @Caption(description = "Termination date advance days for monthly, bi-weekly or yearly rent")
    IPrimitive<Integer> terminationDateAdvanceDaysLongRentPeriod();

    @NotNull
    @Caption(description = "Termination date advance days for weekly or daily rent")
    IPrimitive<Integer> terminationDateAdvanceDaysShortRentPeriod();

    @NotNull
    IPrimitive<Integer> handDeliveryAdvanceDays();

    @NotNull
    IPrimitive<Integer> mailDeliveryAdvanceDays();

    @NotNull
    IPrimitive<Integer> courierDeliveryAdvanceDays();

    @NotNull
    @Caption(description = "N4 will automatically expire after this number of days, if no action (i.e. eviction) is taken")
    IPrimitive<Integer> expiryDays();

    @NotNull
    @Caption(description = "N4 will be cancelled automatically if tenant's outstanding balance is equal or below this value")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> cancellationThreshold();
}
