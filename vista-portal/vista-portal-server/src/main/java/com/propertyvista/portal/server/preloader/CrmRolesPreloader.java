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

    public static final String DEFAULT_COMMANDANT_ROLE_NAME = "Commandant";

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

    public static CrmRole getCommandantRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), CrmRolesPreloader.DEFAULT_COMMANDANT_ROLE_NAME));
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;

    }

    public static CrmRole getPropertyVistaAccountOwnerRole() {
        EntityQueryCriteria<CrmRole> criteria = EntityQueryCriteria.create(CrmRole.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), VistaCrmBehavior.PropertyVistaAccountOwner_OLD.name()));
        CrmRole role = Persistence.service().retrieve(criteria);
        assert (role != null);
        return role;
    }

    @Override
    public String create() {
        List<VistaCrmBehavior> allRoles = new ArrayList<VistaCrmBehavior>(Arrays.asList(VistaCrmBehavior.values()));
        allRoles.remove(VistaCrmBehavior.PropertyVistaSupport);
        allRoles.remove(VistaCrmBehavior.PropertyVistaAccountOwner_OLD);
        if (!ApplicationMode.isDevelopment()) {
            allRoles.remove(VistaCrmBehavior.OAPI);
        }

        createRole(DEFAULT_ACCESS_ALL_ROLE_NAME, true, allRoles.toArray(new VistaCrmBehavior[allRoles.size()]));

        List<VistaCrmBehavior> allNewRoles = new ArrayList<>();
        List<VistaCrmBehavior> allOldRoles = new ArrayList<>();
        for (VistaCrmBehavior behavior : VistaCrmBehavior.values()) {
            if (behavior.name().endsWith("_OLD")) {
                allOldRoles.add(behavior);
            } else {
                allNewRoles.add(behavior);
            }

        }
        createRole("All NEW", true, allNewRoles.toArray(new VistaCrmBehavior[allNewRoles.size()]));
        createRole("All OLD", true, allOldRoles.toArray(new VistaCrmBehavior[allOldRoles.size()]));

        createRole("Accountant_OLD", VistaCrmBehavior.ProductCatalog_OLD, VistaCrmBehavior.Billing_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Accounting_OLD", true, VistaCrmBehavior.PropertyManagement_OLD, VistaCrmBehavior.Organization_OLD, VistaCrmBehavior.Contacts_OLD,
                VistaCrmBehavior.Reports_OLD);
        createRole("Admin_OLD", true, VistaCrmBehavior.Organization_OLD, VistaCrmBehavior.Contacts_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("AM_OLD", VistaCrmBehavior.Contacts_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Asset Manager_OLD", true, VistaCrmBehavior.PropertyManagement_OLD, VistaCrmBehavior.BuildingFinancial_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("BR_OLD", VistaCrmBehavior.Tenants_OLD, VistaCrmBehavior.Emergency_OLD, VistaCrmBehavior.ScreeningData_OLD, VistaCrmBehavior.Occupancy_OLD,
                VistaCrmBehavior.Maintenance_OLD, VistaCrmBehavior.Contacts_OLD, VistaCrmBehavior.Reports_OLD);
        createRole(DEFAULT_COMMANDANT_ROLE_NAME, VistaCrmBehavior.Tenants_OLD, VistaCrmBehavior.Commandant_OLD, VistaCrmBehavior.Emergency_OLD,
                VistaCrmBehavior.ScreeningData_OLD, VistaCrmBehavior.Maintenance_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Communication Manager_OLD", VistaCrmBehavior.MessageGroup_OLD);
        createRole("Executive_OLD", VistaCrmBehavior.Organization_OLD, VistaCrmBehavior.Contacts_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Leasing_OLD", VistaCrmBehavior.Tenants_OLD, VistaCrmBehavior.Equifax_OLD, VistaCrmBehavior.ScreeningData_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Legal_OLD", VistaCrmBehavior.Equifax_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Maintenance_OLD", VistaCrmBehavior.Mechanicals_OLD, VistaCrmBehavior.Occupancy_OLD, VistaCrmBehavior.Maintenance_OLD,
                VistaCrmBehavior.Reports_OLD);
        createRole("Marketing and Leasing Specialist_OLD", VistaCrmBehavior.Marketing_OLD, VistaCrmBehavior.MarketingMedia_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Mechanical Engineer_OLD", VistaCrmBehavior.Mechanicals_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("Owner_OLD", true, VistaCrmBehavior.BuildingFinancial_OLD, VistaCrmBehavior.Reports_OLD);
        createRole("PM_OLD", VistaCrmBehavior.Tenants_OLD, VistaCrmBehavior.Emergency_OLD, VistaCrmBehavior.ScreeningData_OLD, VistaCrmBehavior.Occupancy_OLD,
                VistaCrmBehavior.Contacts_OLD, VistaCrmBehavior.Reports_OLD);

        createRole(VistaCrmBehavior.PropertyVistaAccountOwner_OLD.name(), true, VistaCrmBehavior.PropertyVistaAccountOwner_OLD);
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
