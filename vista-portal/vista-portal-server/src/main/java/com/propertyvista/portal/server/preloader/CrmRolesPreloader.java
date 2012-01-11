/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Arrays;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class CrmRolesPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(CrmRolesPreloader.class);

    private int rolesCount;

    private CrmRole createRole(String name, VistaCrmBehavior... behavior) {
        CrmRole role = EntityFactory.create(CrmRole.class);
        role.name().setValue(name);
        role.behaviors().addAll(Arrays.asList(behavior));
        Persistence.service().persist(role);
        rolesCount++;
        return role;
    }

    @Override
    public String create() {
        createRole("All", VistaCrmBehavior.values());
        if (ApplicationMode.isDevelopment()) {
            for (VistaCrmBehavior behavior : EnumSet.allOf(VistaCrmBehavior.class)) {
                createRole("Test-" + behavior.name(), behavior);
            }
        }

        //TODO Add roles reload with proper business names.

        return "Created " + rolesCount + " Roles";
    }

    @Override
    public String delete() {
        return deleteAll(CrmRole.class);
    }

}
