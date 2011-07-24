/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.AccountCreationRequest;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.server.TestUtil;
import com.propertyvista.portal.server.generator.BusinessDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class ActivationServiceTest extends VistaDBTestBase {
    private final static Logger log = LoggerFactory.getLogger(ActivationServiceTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new VistaDataPreloaders(PreloadConfig.createTest()).preloadAll(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    private ActivationService createService() {
        return TestServiceFactory.create(ActivationService.class);
    }

    public void testUnitExistsEmpty() {
        UnitSelectionCriteria criteria = EntityFactory.create(UnitSelectionCriteria.class);

        ActivationService service = createService();
        service.unitExists(new UnitTestsAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                log.info("Received {}", result);
                Assert.assertFalse("No unit found", result.booleanValue());
            }
        }, criteria);
    }

    public void testUnitExistsWrongData() {
        UnitSelectionCriteria criteria = EntityFactory.create(UnitSelectionCriteria.class);
        criteria.floorplanName().setValue("DoesNotExist");
        criteria.propertyCode().setValue("DoesNotExistAsWell");
        criteria.availableFrom().setValue(new LogicalDate());
        criteria.availableTo().setValue(new LogicalDate());

        ActivationService service = createService();
        service.unitExists(new UnitTestsAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                log.info("Received {}", result);
                Assert.assertFalse("No unit found", result.booleanValue());
            }
        }, criteria);
    }

    public void testUnitExists() {
        UnitSelectionCriteria criteria = EntityFactory.create(UnitSelectionCriteria.class);
        criteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);
        criteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);

        ActivationService service = createService();
        service.unitExists(new UnitTestsAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Assert.assertTrue("Units found", result.booleanValue());
            }
        }, criteria);
    }

    /**
     * Submit a simple account creation request
     */
    public void testCreateAccount() {
        HappyPath.step1createAccount();
    }

    /**
     * Test invalid email address
     */
    public void testCreateAccountInvalidEmail() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = "abc"; // this is invalid email
        request.email().setValue(email);
        request.password().setValue("1234");
        request.captcha().setValue(TestUtil.createCaptcha());

        ActivationService service = createService();
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.fail("Should never come here");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Assert.assertNotNull("Received failure", throwable);
                Assert.assertEquals(UserRuntimeException.class, throwable.getClass());
            }
        }, request);
    }

    public void testCreateAccountNoPassword() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = BusinessDataGenerator.createEmail();
        request.email().setValue(email);
        request.captcha().setValue(TestUtil.createCaptcha());

        ActivationService service = createService();
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.fail("Should never come here");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Assert.assertNotNull("Received failure", throwable);
                Assert.assertEquals(UserRuntimeException.class, throwable.getClass());
            }
        }, request);
    }

    public void testCreateAccountNoCaptcha() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = BusinessDataGenerator.createEmail();
        request.email().setValue(email);
        request.password().setValue("abc-password");

        ActivationService service = createService();
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.fail("Should never come here");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Assert.assertNotNull("Received failure", throwable);
                Assert.assertEquals(UserRuntimeException.class, throwable.getClass());
            }
        }, request);
    }

    public void testCreateAccountAlreadyExists() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = BusinessDataGenerator.createEmail();
        request.email().setValue(email);
        request.password().setValue("1234");
        request.captcha().setValue(TestUtil.createCaptcha());

        ActivationService service = createService();
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.assertNotNull("Got the visit", result.getUserVisit());
                Assert.assertEquals("Email is correct", email, result.getUserVisit().getEmail());
            }
        }, request);

        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.fail("Should not see success");
            }

            @Override
            public void onFailure(Throwable throwable) {
                // this means that there was a problem creating the same account
                Assert.assertNotNull("Received failure", throwable);
                Assert.assertEquals(UserRuntimeException.class, throwable.getClass());
            }
        }, request);
    }
}
