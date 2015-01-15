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
    IPrimitive<String> evictionFlowStep();

    // N4 Signing Agent data
    @NotNull
    @Caption(name = "Agent Selection Method")
    IPrimitive<EmployeeSelectionMethod> agentSelectionMethodN4();

    @Caption(name = "Use Agent Signature")
    IPrimitive<Boolean> useAgentSignatureN4();

    @Caption(name = "Use Agent Contact Info")
    IPrimitive<Boolean> useAgentContactInfoN4();

    @Editor(type = EditorType.phone)
    @Caption(name = "Phone")
    IPrimitive<String> phoneNumber();

    @Editor(type = EditorType.phone)
    @Caption(name = "Fax")
    IPrimitive<String> faxNumber();

    @Editor(type = EditorType.email)
    @Caption(name = "E-Mail")
    IPrimitive<String> emailAddress();

    // N4 Servicing Agent data
    @NotNull
    @Caption(name = "Agent Selection Method")
    IPrimitive<EmployeeSelectionMethod> agentSelectionMethodCS();

    @Caption(name = "Use Agent Signature")
    IPrimitive<Boolean> useAgentSignatureCS();

    @Caption(name = "Use Agent Contact Info")
    IPrimitive<Boolean> useAgentContactInfoCS();

    @Editor(type = EditorType.phone)
    @Caption(name = "Phone")
    IPrimitive<String> phoneNumberCS();

    @NotNull
    IPrimitive<String> companyName();

    @EmbeddedEntity
    InternationalAddress mailingAddress();

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
