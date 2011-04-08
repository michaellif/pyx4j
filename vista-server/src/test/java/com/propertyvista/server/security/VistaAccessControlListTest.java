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

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.domain.pt.PaymentInfo;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.rpc.pt.PtUserVisit;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.rpc.pt.services.ApartmentService;
import com.propertyvista.portal.rpc.pt.services.ApplicationService;
import com.propertyvista.portal.rpc.pt.services.ChargesService;
import com.propertyvista.portal.rpc.pt.services.PaymentService;
import com.propertyvista.portal.rpc.pt.services.PetService;
import com.propertyvista.portal.rpc.pt.services.SummaryService;
import com.propertyvista.portal.rpc.pt.services.TenantFinancialService;
import com.propertyvista.portal.rpc.pt.services.TenantInfoService;
import com.propertyvista.portal.rpc.pt.services.TenantService;
import com.propertyvista.portal.server.pt.PtAppContext;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.unit.server.mock.TestLifecycle;

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
        assertPermission(false, PetService.class);
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
        assertPermission(true, PetService.class);
        assertPermission(true, ChargesService.class);
        assertPermission(true, SummaryService.class);
        assertPermission(true, PaymentService.class);

        TestLifecycle.endRequest();
    }

    void assertEntityPermission(boolean expected, Class<? extends IEntity> entityClass) {
        try {
            IEntity ent = EntityFactory.create(entityClass);
            if ((ent instanceof IBoundToApplication) && Context.isUserLoggedIn()) {
                ((IBoundToApplication) ent).application().set(PtAppContext.getCurrentUserApplication());
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

        assertEntityPermission(false, ApplicationProgress.class);
        assertEntityPermission(false, UnitSelection.class);
        assertEntityPermission(false, ApplicationDocument.class);
        assertEntityPermission(false, PotentialTenantList.class);
        assertEntityPermission(false, PotentialTenantInfo.class);
        assertEntityPermission(false, Pets.class);
        assertEntityPermission(false, PotentialTenantFinancial.class);
        assertEntityPermission(false, Charges.class);
        assertEntityPermission(false, Summary.class);
        assertEntityPermission(false, PaymentInfo.class);
    }

    @Test
    public void tenantApplicationEntityInstanceAccess() {
        TestLifecycle.testSession(new PtUserVisit(-101L, "bob"), VistaBehavior.POTENTIAL_TENANT);
        TestLifecycle.beginRequest();

        Application application = EntityFactory.create(Application.class);
        application.setPrimaryKey(-251L);
        PtAppContext.setCurrentUserApplication(application);

        assertEntityPermission(true, ApplicationProgress.class);
        assertEntityPermission(true, UnitSelection.class);
        assertEntityPermission(true, ApplicationDocument.class);
        assertEntityPermission(true, PotentialTenantList.class);
        assertEntityPermission(true, PotentialTenantInfo.class);
        assertEntityPermission(true, Pets.class);
        assertEntityPermission(true, PotentialTenantFinancial.class);
        assertEntityPermission(true, Charges.class);
        assertEntityPermission(true, Summary.class);
        assertEntityPermission(true, PaymentInfo.class);
    }

}
