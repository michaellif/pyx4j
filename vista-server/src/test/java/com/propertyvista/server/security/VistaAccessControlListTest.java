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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.ApplicationDocument;
import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.portal.domain.ptapp.ApplicationProgress;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.PtUserVisit;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;
import com.propertyvista.portal.rpc.ptapp.services.AddonsService;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

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
        assertPermission(true, ActivationService.class);

        assertPermission(false, ApplicationService.class);
        assertPermission(false, ApartmentService.class);
        assertPermission(false, TenantService.class);
        assertPermission(false, TenantInfoService.class);
        assertPermission(false, TenantFinancialService.class);
        assertPermission(false, AddonsService.class);
        assertPermission(false, ChargesService.class);
        assertPermission(false, SummaryService.class);
        assertPermission(false, PaymentService.class);
        TestLifecycle.endRequest();
    }

    @Test
    public void tenantServicePermissions() {
        TestLifecycle.testSession(null, VistaBehavior.POTENTIAL_TENANT);
        TestLifecycle.beginRequest();
        assertPermission(true, ActivationService.class);

        assertPermission(true, ApplicationService.class);
        assertPermission(true, ApartmentService.class);
        assertPermission(true, TenantService.class);
        assertPermission(true, TenantInfoService.class);
        assertPermission(true, TenantFinancialService.class);
        assertPermission(true, AddonsService.class);
        assertPermission(true, ChargesService.class);
        assertPermission(true, SummaryService.class);
        assertPermission(true, PaymentService.class);

        TestLifecycle.endRequest();
    }

    void assertEntityPermission(boolean expected, Class<? extends IEntity> entityClass, Application application) {
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

        assertEntityPermission(false, ApplicationProgress.class, null);
        assertEntityPermission(false, UnitSelection.class, null);
        assertEntityPermission(false, ApplicationDocument.class, null);
        assertEntityPermission(false, Tenant.class, null);
        assertEntityPermission(false, PotentialTenantInfo.class, null);
        assertEntityPermission(false, PetsDTO.class, null);
        assertEntityPermission(false, TenantFinancialDTO.class, null);
        assertEntityPermission(false, Charges.class, null);
        assertEntityPermission(false, Summary.class, null);
        assertEntityPermission(false, PaymentInfo.class, null);
    }

    @Test
    public void tenantApplicationEntityInstanceAccess() {
        TestLifecycle.testSession(new PtUserVisit(new Key(-101), "bob"), VistaBehavior.POTENTIAL_TENANT);
        TestLifecycle.beginRequest();

        Application application = EntityFactory.create(Application.class);
        application.setPrimaryKey(new Key(-251));
        PtAppContext.setCurrentUserApplication(application);

        assertEntityPermission(true, ApplicationProgress.class, application);
        assertEntityPermission(true, UnitSelection.class, application);
        assertEntityPermission(true, ApplicationDocument.class, application);
        assertEntityPermission(true, Tenant.class, application);
        assertEntityPermission(true, PotentialTenantInfo.class, application);
        assertEntityPermission(true, PetsDTO.class, application);
        assertEntityPermission(true, TenantFinancialDTO.class, application);
        assertEntityPermission(true, Charges.class, application);
        assertEntityPermission(true, Summary.class, application);
        assertEntityPermission(true, PaymentInfo.class, application);

        Application application2 = EntityFactory.create(Application.class);
        application2.setPrimaryKey(new Key(-252));
        assertEntityPermission(false, ApplicationProgress.class, application2);
        assertEntityPermission(false, UnitSelection.class, application2);
        assertEntityPermission(false, ApplicationDocument.class, application2);
        assertEntityPermission(false, Tenant.class, application2);
        assertEntityPermission(false, PotentialTenantInfo.class, application2);
        assertEntityPermission(false, PetsDTO.class, application2);
        assertEntityPermission(false, TenantFinancialDTO.class, application2);
        assertEntityPermission(false, Charges.class, application2);
        assertEntityPermission(false, Summary.class, application2);
        assertEntityPermission(false, PaymentInfo.class, application2);
    }

}
