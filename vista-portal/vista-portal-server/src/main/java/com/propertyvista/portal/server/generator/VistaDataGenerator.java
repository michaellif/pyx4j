/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.generator;

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.entity.shared.EntityFactory;

public class VistaDataGenerator {

    public static User createUser() {
        User user = EntityFactory.create(User.class);
        user.name().setValue("Gregory Holmes");
        user.email().setValue("gregory@221b.com");

        UserCredential credential = EntityFactory.create(UserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword("london"));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(VistaBehavior.POTENTIAL_TENANT);

        return user;
    }

    public static Application createApplication(User user) {
        Application application = EntityFactory.create(Application.class);
        application.user().set(user);
        return application;
    }

}
