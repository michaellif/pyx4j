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
package com.propertyvista.biz.preloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.generator.LocationsGenerator;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class CrmRolesPreloader extends AbstractDataPreloader {

    public static final String DEFAULT_ACCESS_ALL_ROLE_NAME = "All";

    public static final String DEFAULT_SUPPORT_ROLE_NAME = "PropertyVista Support";

    private final static Logger log = LoggerFactory.getLogger(CrmRolesPreloader.class);

    private int rolesCount;

    private CrmRole createRole(String name, VistaCrmBehavior... behavior) {
        return createRole(name, false, behavior);
    }

    private CrmRole createRole(String name, boolean requireSecurityQuestionForPasswordReset, VistaCrmBehavior... behavior) {
        CrmRole role = EntityFactory.create(CrmRole.class);
        role.name().setValue(name);
        role.behaviors().addAll(Arrays.asList(behavior));
        role.requireSecurityQuestionForPasswordReset().setValue(requireSecurityQuestionForPasswordReset);
        Persistence.service().persist(role);
        rolesCount++;
        return role;
    }

    public static CrmRole getDefaultRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.eq(criteria.proto().name(), CrmRolesPreloader.DEFAULT_ACCESS_ALL_ROLE_NAME);
        return Persistence.service().retrieve(criteria);
    }

    public static CrmRole getSupportRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.eq(criteria.proto().name(), CrmRolesPreloader.DEFAULT_SUPPORT_ROLE_NAME);
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;
    }

    public static CrmRole getOapiRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.eq(criteria.proto().behaviors(), VistaCrmBehavior.OAPI_Properties);
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public String create() {
        List<VistaCrmBehavior> allRoles = new ArrayList<VistaCrmBehavior>(Arrays.asList(VistaCrmBehavior.values()));
        allRoles.remove(VistaCrmBehavior.PropertyVistaSupport);
        allRoles.remove(VistaCrmBehavior.OAPI_Properties);
        allRoles.remove(VistaCrmBehavior.OAPI_ILS);

        createRole(DEFAULT_ACCESS_ALL_ROLE_NAME, true, allRoles.toArray(new VistaCrmBehavior[allRoles.size()]));

        createRole(DEFAULT_SUPPORT_ROLE_NAME, VistaCrmBehavior.PropertyVistaSupport);

        Persistence.service().persist(
                EntityCSVReciver.create(SecurityQuestion.class).loadResourceFile(IOUtils.resourceFileName("SecurityQuestion.csv", LocationsGenerator.class)));

        return "Created " + rolesCount + " Roles";
    }

    @Override
    public String delete() {
        return deleteAll(CrmRole.class);
    }

}
