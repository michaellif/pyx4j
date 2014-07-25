/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.util.Arrays;
import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class CrmRolesDevPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        for (VistaCrmBehavior behavior : EnumSet.allOf(VistaCrmBehavior.class)) {
            createRole("Test-" + behavior.name(), behavior);
        }
        return null;
    }

    private CrmRole createRole(String name, VistaCrmBehavior... behavior) {
        return createRole(name, false, behavior);
    }

    private CrmRole createRole(String name, boolean requireSecurityQuestionForPasswordReset, VistaCrmBehavior... behavior) {
        CrmRole role = EntityFactory.create(CrmRole.class);
        role.name().setValue(name);
        role.behaviors().addAll(Arrays.asList(behavior));
        role.requireSecurityQuestionForPasswordReset().setValue(requireSecurityQuestionForPasswordReset);
        Persistence.service().persist(role);
        return role;
    }

    @Override
    public String delete() {
        return null;
    }

}
