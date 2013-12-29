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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.generator.LocationsGenerator;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class CrmRolesPreloader extends BaseVistaDevDataPreloader {

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
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), CrmRolesPreloader.DEFAULT_ACCESS_ALL_ROLE_NAME));
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;

    }

    public static CrmRole getSupportRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), CrmRolesPreloader.DEFAULT_SUPPORT_ROLE_NAME));
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;

    }

    public static CrmRole getPropertyVistaAccountOwnerRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), VistaCrmBehavior.PropertyVistaAccountOwner.name()));
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;
    }

    @Override
    public String create() {
        List<VistaCrmBehavior> allRoles = new ArrayList<VistaCrmBehavior>(Arrays.asList(VistaCrmBehavior.values()));
        allRoles.remove(VistaCrmBehavior.PropertyVistaSupport);
        allRoles.remove(VistaCrmBehavior.PropertyVistaAccountOwner);
        if (!ApplicationMode.isDevelopment()) {
            allRoles.remove(VistaCrmBehavior.OAPI);
        }

        createRole(DEFAULT_ACCESS_ALL_ROLE_NAME, true, allRoles.toArray(new VistaCrmBehavior[allRoles.size()]));

        createRole("Accountant", VistaCrmBehavior.ProductCatalog, VistaCrmBehavior.Billing, VistaCrmBehavior.Reports);
        createRole("Accounting", true, VistaCrmBehavior.PropertyManagement, VistaCrmBehavior.Organization, VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);
        createRole("Admin", true, VistaCrmBehavior.Organization, VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);
        createRole("AM", VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);
        createRole("Asset Manager", true, VistaCrmBehavior.PropertyManagement, VistaCrmBehavior.BuildingFinancial, VistaCrmBehavior.Reports);
        createRole("BR", VistaCrmBehavior.Tenants, VistaCrmBehavior.Emergency, VistaCrmBehavior.ScreeningData, VistaCrmBehavior.Occupancy,
                VistaCrmBehavior.Maintenance, VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);
        createRole("Executive", VistaCrmBehavior.Organization, VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);
        createRole("Leasing", VistaCrmBehavior.Tenants, VistaCrmBehavior.Equifax, VistaCrmBehavior.ScreeningData, VistaCrmBehavior.Reports);
        createRole("Legal", VistaCrmBehavior.Equifax, VistaCrmBehavior.Reports);
        createRole("Maintenance", VistaCrmBehavior.Mechanicals, VistaCrmBehavior.Occupancy, VistaCrmBehavior.Maintenance, VistaCrmBehavior.Reports);
        createRole("Marketing and Leasing Specialist", VistaCrmBehavior.Marketing, VistaCrmBehavior.MarketingMedia, VistaCrmBehavior.Reports);
        createRole("Mechanical Engineer", VistaCrmBehavior.Mechanicals, VistaCrmBehavior.Reports);
        createRole("Owner", true, VistaCrmBehavior.BuildingFinancial, VistaCrmBehavior.Reports);
        createRole("PM", VistaCrmBehavior.Tenants, VistaCrmBehavior.Emergency, VistaCrmBehavior.ScreeningData, VistaCrmBehavior.Occupancy,
                VistaCrmBehavior.Contacts, VistaCrmBehavior.Reports);

        createRole(VistaCrmBehavior.PropertyVistaAccountOwner.name(), true, VistaCrmBehavior.PropertyVistaAccountOwner);
        createRole(DEFAULT_SUPPORT_ROLE_NAME, VistaCrmBehavior.PropertyVistaSupport);

        if (ApplicationMode.isDevelopment()) {
            for (VistaCrmBehavior behavior : EnumSet.allOf(VistaCrmBehavior.class)) {
                createRole("Test-" + behavior.name(), behavior);
            }
        }

        //TODO Add roles reload with proper business names.

        Persistence.service().persist(
                EntityCSVReciver.create(SecurityQuestion.class).loadResourceFile(IOUtils.resourceFileName("SecurityQuestion.csv", LocationsGenerator.class)));

        return "Created " + rolesCount + " Roles";
    }

    @Override
    public String delete() {
        return deleteAll(CrmRole.class);
    }

}
