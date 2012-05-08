/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.preloader;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;

public class OnboardingPmcPreloader extends AbstractDataPreloader {

    public static Pmc createOnboardingPmc(String name, String dnsName, String onboardingAccountId) {

        if (!PmcNameValidator.canCreatePmcName(dnsName))
            return null;

        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.dnsName().setValue(dnsName);
        pmc.namespace().setValue(dnsName.replace('-', '_'));
        pmc.name().setValue(name);
        pmc.onboardingAccountId().setValue(onboardingAccountId);

        // TODO For future
//        for (String dndAlias : request.dnsNameAliases()) {
//
//        }

        pmc.enabled().setValue(Boolean.TRUE);
        Persistence.service().persist(pmc);

        return pmc;
    }

    @Override
    public String create() {

        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
