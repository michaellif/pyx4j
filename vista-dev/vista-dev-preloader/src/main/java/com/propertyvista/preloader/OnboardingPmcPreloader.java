/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jun 12, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.server.jobs.TaskRunner;

public class OnboardingPmcPreloader extends AbstractDataPreloader {
    private final static Logger log = LoggerFactory.getLogger(OnboardingPmcPreloader.class);

    public static Pmc createPmc(String name, String dnsName, String onboardingAccountId) {
        EntityQueryCriteria<OnboardingUserCredential> credentialCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
        credentialCrt.add(PropertyCriterion.eq(credentialCrt.proto().onboardingAccountId(), onboardingAccountId));
        List<OnboardingUserCredential> creds = Persistence.service().query(credentialCrt);

        if (creds.size() == 0) {
            log.debug("No users for PMC " + name);
            return null;
        }

        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.dnsName().setValue(dnsName);
        pmc.namespace().setValue(dnsName.replace('-', '_'));
        pmc.name().setValue(name);
        pmc.onboardingAccountId().setValue(onboardingAccountId);

        // TODO For future
//        for (String dndAlias : request.dnsNameAliases()) {
//
//        }

        pmc.status().setValue(PmcStatus.Created);
        Persistence.service().persist(pmc);

        for (OnboardingUserCredential cred : creds) {
            cred.pmc().set(pmc);

            cred.onboardingAccountId().setValue(null); // We will lookup by pmc
            Persistence.service().persist(cred);
        }

        Persistence.service().commit();

        return pmc;
    }

    @Override
    public String create() {

        return TaskRunner.runInAdminNamespace(new Callable<String>() {
            @Override
            public String call() {

                OnboardingUserPreloader.createOnboardingUser("Ostap", "Way", "OstapWay@gmail.com", "12345", VistaOnboardingBehavior.ProspectiveClient, "1");
                OnboardingUserPreloader.createOnboardingUser("Pon", "Svirey", "PonSvirey@gmail.com", "12345", VistaOnboardingBehavior.ProspectiveClient, "2");
                OnboardingUserPreloader.createOnboardingUser("Gramper", "33", "Gramper33@gmail.com", "12345", VistaOnboardingBehavior.ProspectiveClient, "3");
                OnboardingUserPreloader.createOnboardingUser("Rennytona", "17", "rennytona17@gmail.com", "12345", VistaOnboardingBehavior.ProspectiveClient,
                        "4");
                OnboardingUserPreloader.createOnboardingUser("Georgey", "Ggg", "GeorgeyGgg@gmail.com", "12345", VistaOnboardingBehavior.ProspectiveClient, "5");

                OnboardingPmcPreloader.createPmc("Free", "pmcFree", "1");
                OnboardingPmcPreloader.createPmc("PMC User(1-5)", "pmc15", "2");
                OnboardingPmcPreloader.createPmc("PMC User(6-14)", "pmc614", "3");
                OnboardingPmcPreloader.createPmc("PMC User(15-49)", "pmc1549", "4");
                OnboardingPmcPreloader.createPmc("PMC User(over 50)", "pmcOver50", "5");

                return "Created " + 5 + " PMCs for Onboarding";
            }
        });
    }

    @Override
    public String delete() {

        return null;
    }

}
