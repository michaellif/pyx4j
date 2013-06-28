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
package com.propertyvista.operations.rpc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.OnboardingUser;

@Transient
@Caption(name = "Property Management Company (PMC)")
@ExtendsDBO(Pmc.class)
public interface PmcDTO extends Pmc {

    OnboardingUser createPmcForExistingOnboardingUserRequest();

    Person person();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @NotNull
    @Editor(type = EditorType.password)
    @LogTransient
    IPrimitive<String> password();

    IPrimitive<String> vistaCrmUrl();

    IPrimitive<String> residentPortalUrl();

    IPrimitive<String> prospectPortalUrl();

}
