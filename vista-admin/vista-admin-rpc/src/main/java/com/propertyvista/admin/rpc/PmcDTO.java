/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.fee.PmcEquifaxFee;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.settings.PmcVistaFeatures;

@Transient
@Caption(name = "Property Management Company (PMC)")
public interface PmcDTO extends IEntity {

    OnboardingUser createPmcForExistingOnboardingUserRequest();

    IPrimitive<PmcStatus> status();

    @NotNull
    @Caption(name = "Company name")
    IPrimitive<String> name();

    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> namespace();

    @NotNull
    @Caption(name = "DNS name")
    IPrimitive<String> dnsName();

    IList<PmcDnsName> dnsNameAliases();

    PmcVistaFeatures features();

    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> onboardingAccountId();

    Person person();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @NotNull
    @Editor(type = EditorType.password)
    @Transient(logTransient = true)
    IPrimitive<String> password();

    IPrimitive<String> accountStatus_TBD();

    IPrimitive<String> vistaCrmUrl();

    IPrimitive<String> residentPortalUrl();

    IPrimitive<String> prospectPortalUrl();

    @ReadOnly
    IPrimitive<Date> created();

    @ReadOnly
    IPrimitive<Date> updated();

    PmcEquifaxInfo equifaxInfo();

    PmcEquifaxFee equifaxFeeQuote();

}
