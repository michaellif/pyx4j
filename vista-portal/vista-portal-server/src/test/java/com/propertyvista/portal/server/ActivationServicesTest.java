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

import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.services.ActivationServices;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.MockServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;

public class ActivationServicesTest extends VistaDBTestCase {

    public void testNoting() throws Exception {
    }

    public void TODO_testAccountCreation() {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        final String email = DataGenerator.randomLastName() + DataGenerator.randomInt(Integer.MAX_VALUE) + DemoData.USERS_DOMAIN;
        request.email().setValue(email);
        request.password().setValue("1234");
        request.captcha().setValue(new Pair<String, String>("n/a", "x"));

        ActivationServices service = MockServiceFactory.create(ActivationServices.class);
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.assertNotNull("Git the visit", result.getUserVisit());
                Assert.assertEquals("email", email, result.getUserVisit().getEmail());

            }
        }, request);
    }
}
