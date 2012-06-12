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

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class OnboardingUserPreloader extends AbstractDataPreloader {

    public static OnboardingUserCredential createOnboardingUser(String name, String email, String password, VistaOnboardingBehavior role,
            String onboardingAccountId) {

        return createOnboardingUser(null, null, name, email, password, role, onboardingAccountId);
    }

    public static OnboardingUserCredential createOnboardingUser(String firstName, String lastName, String email, String password, VistaOnboardingBehavior role,
            String onboardingAccountId) {

        return createOnboardingUser(firstName, lastName, firstName + " " + lastName, email, password, role, onboardingAccountId);

    }

    private static OnboardingUserCredential createOnboardingUser(String firstName, String lastName, String name, String email, String password,
            VistaOnboardingBehavior role, String onboardingAccountId) {
        OnboardingUser user = EntityFactory.create(OnboardingUser.class);
        user.firstName().setValue(firstName);
        user.lastName().setValue(lastName);
        user.name().setValue(name);
        user.email().setValue(email);
        Persistence.service().persist(user);

        OnboardingUserCredential credential = EntityFactory.create(OnboardingUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.behavior().setValue(role);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.onboardingAccountId().setValue(onboardingAccountId);

        Persistence.service().persist(credential);

        Persistence.service().commit();

        return credential;
    }

    @Override
    public String create() {
        createOnboardingUser("Roman Spakovych", "romans@rossul.com", "romans@rossul.com", VistaOnboardingBehavior.OnboardingAdministrator, null);
        createOnboardingUser("Vista Equifax", "vista.equifax@rossul.com", "vista.equifax@rossul.com", VistaOnboardingBehavior.Equifax, null);
        createOnboardingUser("Vista Caledon", "vista.caledon@rossul.com", "vista.caledon@rossul.com", VistaOnboardingBehavior.Caledon, null);
        createOnboardingUser("Prospective Client", "prospective.client@rossul.com", "prospective.client@rossul.com", VistaOnboardingBehavior.ProspectiveClient,
                null);

        return "Created " + 3 + " Onboarding Users";
    }

    @Override
    public String delete() {
        return null;
    }

}
