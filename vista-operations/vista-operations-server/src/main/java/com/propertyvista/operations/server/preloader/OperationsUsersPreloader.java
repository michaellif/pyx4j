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
package com.propertyvista.operations.server.preloader;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.shared.config.VistaDemo;

class OperationsUsersPreloader extends AbstractDataPreloader {

    public static OperationsUser createAdminUser(String name, String email, String password, VistaOperationsBehavior behaivior) {
        OperationsUser user = EntityFactory.create(OperationsUser.class);
        user.name().setValue(name);
        user.email().setValue(email);
        Persistence.service().persist(user);

        OperationsUserCredential credential = EntityFactory.create(OperationsUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behaviors().add(behaivior);

        Persistence.service().persist(credential);

        return user;
    }

    @Override
    public String create() {

        int cnt = 0;
        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            int a = 1;
            for (VistaOperationsBehavior behavior : VistaOperationsBehavior.values()) {
                String email = DemoData.UserType.ADMIN.getEmail(a);
                createAdminUser(email + "(" + behavior + ")", email, email, behavior);
                a++;
                cnt++;
            }
        }
        cnt += 4;
        if (!VistaDemo.isDemo()) {
            createAdminUser("PropertyVista Support", "support@propertyvista.com", rnd4prod("support@propertyvista.com"), VistaOperationsBehavior.SystemAdmin);
            createAdminUser("VladS", "vlads@propertyvista.com", rnd4prod("vlads@propertyvista.com"), VistaOperationsBehavior.SecurityAdmin);
            createAdminUser("VictorV", "vvassiliev@propertyvista.com", rnd4prod("vvassiliev@propertyvista.com"), VistaOperationsBehavior.SystemAdmin);
            createAdminUser("AlexK", "akinareevski@propertyvista.com", rnd4prod("akinareevski@propertyvista.com"), VistaOperationsBehavior.SystemAdmin);
            createAdminUser("YuriyL", "yuriyl@propertyvista.com", rnd4prod("yuriyl@propertyvista.com"), VistaOperationsBehavior.SystemAdmin);
        }

        return "Created " + cnt + " Admin Users";
    }

    private String rnd4prod(String string) {
        if (ApplicationMode.isDevelopment()) {
            return string;
        } else {
            return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(14) + 4);
        }
    }

    @Override
    public String delete() {
        return null;
    }

}
