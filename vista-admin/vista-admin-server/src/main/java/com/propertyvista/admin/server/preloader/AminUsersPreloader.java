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

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.VistaAdminBehavior;
import com.propertyvista.server.common.security.PasswordEncryptor;

class AminUsersPreloader extends AbstractDataPreloader {

    public static AdminUser createAdminUser(String name, String email, String password, VistaAdminBehavior behaivior) {
        AdminUser user = EntityFactory.create(AdminUser.class);
        user.name().setValue(name);
        user.email().setValue(email);
        Persistence.service().persist(user);

        AdminUserCredential credential = EntityFactory.create(AdminUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behaviors().add(behaivior);

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {

        int cnt = 0;
        if (ApplicationMode.isDevelopment()) {
            for (int i = 1; i <= DemoData.UserType.ADMIN.getDefaultMax(); i++) {
                String email = DemoData.UserType.ADMIN.getEmail(i);
                createAdminUser(email, email, email, VistaAdminBehavior.SystemAdmin);
                cnt++;
            }
        }
        cnt += 4;
        createAdminUser("PropertyVista Support", "support@propertyvista.com", "support@propertyvista.com", VistaAdminBehavior.SystemAdmin);
        createAdminUser("VladS", "vlads@propertyvista.com", "vlads@propertyvista.com", VistaAdminBehavior.SystemAdmin);
        createAdminUser("VictorV", "vvassiliev@propertyvista.com", "vvassiliev@propertyvista.com", VistaAdminBehavior.SystemAdmin);
        createAdminUser("Onboarding API", "romans@rossul.com", "secret", VistaAdminBehavior.OnboardingApi);
        createAdminUser("Onboarding API (old)", "rossul", "secret", VistaAdminBehavior.OnboardingApi);

        return "Created " + cnt + " Admin Users";
    }

    @Override
    public String delete() {
        return null;
    }

}
