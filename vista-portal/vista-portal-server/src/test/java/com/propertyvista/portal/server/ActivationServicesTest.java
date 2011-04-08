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
package com.propertyvista.portal.server;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.server.preloader.BusinessDataGenerator;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class ActivationServicesTest extends VistaDBTestCase {
    @SuppressWarnings("unused")
    private final static Logger log = LoggerFactory.getLogger(ActivationServicesTest.class);

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    private ActivationService createService() {
        return TestServiceFactory.create(ActivationService.class);
    }

    /**
     * Test invalid email address
     */
    public void testInvalidEmail() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = "abc";
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

    /**
     * Submit a simple account creation request
     */
    public void testAccountCreation() {
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
    }
}
