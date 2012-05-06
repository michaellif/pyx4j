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

class OnboardingUserPreloader extends AbstractDataPreloader {

    public static OnboardingUser createOnboardingUser(String name, String email, VistaOnboardingBehavior role) {
        OnboardingUser user = EntityFactory.create(OnboardingUser.class);
        user.name().setValue(name);
        user.email().setValue(email);
        Persistence.service().persist(user);

        OnboardingUserCredential credential = EntityFactory.create(OnboardingUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.behavior().setValue(role);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(email));
        credential.enabled().setValue(Boolean.TRUE);

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {
        createOnboardingUser("Roman Spakovych", "romans@rossul.com", VistaOnboardingBehavior.OnboardingAdministrator);
        return "Created " + 1 + " OnboardingUser";
    }

    @Override
    public String delete() {
        return null;
    }

}
