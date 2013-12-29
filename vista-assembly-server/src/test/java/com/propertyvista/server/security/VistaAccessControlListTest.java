/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFile;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;

public class VistaAccessControlListTest {

    private final static Logger log = LoggerFactory.getLogger(VistaAccessControlListTest.class);

    @BeforeClass
    public static void init() throws Exception {
        VistaTestDBSetup.init();
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    public static void debuPermissions() {
        log.info("AclCreator {}", ServerSideConfiguration.instance().getAclCreator());
        log.info("Current Behaviors {}", SecurityController.getBehaviors());
        log.info("AccessControlList {}", ToStringBuilder.reflectionToString(SecurityController.instance().getAcl(), ToStringStyle.MULTI_LINE_STYLE));
    }

    void assertPermission(boolean expected, Class<? extends IService> targetServiceInterface) {
        try {
            Assert.assertEquals("Allow " + targetServiceInterface.getSimpleName(), expected,
                    SecurityController.checkPermission(new IServiceExecutePermission(targetServiceInterface)));
        } catch (AssertionError error) {
            debuPermissions();
            throw error;
        }
    }

    @Test
    public void publicServicePermissions() {
        TestLifecycle.beginRequest();

        TestLifecycle.endRequest();

    }

    @Test
    public void prospectiveTenantServicePermissions() {
        TestLifecycle.testSession(null, PortalProspectBehavior.Prospect);
        TestLifecycle.beginRequest();

        TestLifecycle.endRequest();
    }

    void assertEntityPermission(boolean expected, Class<? extends IEntity> entityClass, OnlineApplication application) {
        try {
            IEntity ent = EntityFactory.create(entityClass);
            if ((application != null) && (ent instanceof IBoundToApplication)) {
                ((IBoundToApplication) ent).application().set(application);
            }
            Assert.assertEquals("Allow Read " + entityClass.getSimpleName(), expected, SecurityController.checkPermission(EntityPermission.permissionRead(ent)));
            Assert.assertEquals("Allow Update " + entityClass.getSimpleName(), expected,
                    SecurityController.checkPermission(EntityPermission.permissionUpdate(ent)));

        } catch (AssertionError error) {
            debuPermissions();
            throw error;
        }
    }

    @Test
    public void publicApplicationEntityInstanceAccess() {
        TestLifecycle.beginRequest();

        assertEntityPermission(false, ProofOfEmploymentDocumentFile.class, null);
        assertEntityPermission(false, TenantInLeaseListDTO.class, null);
        assertEntityPermission(false, LeaseTermTenant.class, null);
        assertEntityPermission(false, TenantFinancialDTO.class, null);

    }

    @Test
    @Ignore
    public void tenantApplicationEntityInstanceAccess() {
        TestLifecycle.testSession(new UserVisit(new Key(-101), "bob"), PortalProspectBehavior.Prospect);
        TestLifecycle.beginRequest();

        OnlineApplication application = EntityFactory.create(OnlineApplication.class);
        application.setPrimaryKey(new Key(-251));
        ProspectPortalContext.setOnlineApplication(application);

        assertEntityPermission(true, ProofOfEmploymentDocumentFile.class, application);
        assertEntityPermission(true, TenantInLeaseListDTO.class, application);
        assertEntityPermission(true, LeaseTermTenant.class, application);
        assertEntityPermission(true, TenantFinancialDTO.class, application);

        OnlineApplication application2 = EntityFactory.create(OnlineApplication.class);
        application2.setPrimaryKey(new Key(-252));
        assertEntityPermission(false, ProofOfEmploymentDocumentFile.class, application2);
        assertEntityPermission(false, TenantInLeaseListDTO.class, application2);
        assertEntityPermission(false, LeaseTermTenant.class, application2);
        assertEntityPermission(false, TenantFinancialDTO.class, application2);
    }

}
