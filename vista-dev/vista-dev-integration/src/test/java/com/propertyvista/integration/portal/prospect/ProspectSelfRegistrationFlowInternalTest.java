/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.portal.prospect;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.EmptyAsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.ProspectSignUpDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class ProspectSelfRegistrationFlowInternalTest extends ProspectInternalTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        getBuilding();
    }

    public void testRegistration() {
        TestLifecycle.endRequest();
        // Logout from CRM
        TestLifecycle.testSession(null);

        ProspectSignUpDTO signUp = EntityFactory.create(ProspectSignUpDTO.class);

        signUp.firstName().setValue(DataGenerator.randomFirstName());
        signUp.lastName().setValue(DataGenerator.randomLastName());
        signUp.email().setValue("t001@pyx4j.com");
        signUp.password().setValue("pwd");

        signUp.ilsBuildingId().setValue(getBuilding().propertyCode().getValue());

        TestServiceFactory.create(ProspectSignUpService.class).signUp(new EmptyAsyncCallbackAssertion<VoidSerializable>(), signUp);

        TestServiceFactory.create(ProspectAuthenticationService.class).authenticate(new EmptyAsyncCallbackAssertion<AuthenticationResponse>(),
                new ClientSystemInfo(), signUp);

        final OnlineApplicationDTO applicationDTOInitial = EntityFactory.create(OnlineApplicationDTO.class);

        // Now we have working session
        TestServiceFactory.create(ApplicationWizardService.class).init(new AsyncCallbackAssertion<OnlineApplicationDTO>() {

            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                applicationDTOInitial.set(result);

            }
        });

        assertEquals("firstName", signUp.firstName(), applicationDTOInitial.applicant().person().name().firstName());

        String newfirstName = "Per. " + DataGenerator.randomFirstName();

        applicationDTOInitial.applicant().person().name().firstName().setValue(newfirstName);

        TestServiceFactory.create(ApplicationWizardService.class).save(new EmptyAsyncCallbackAssertion<Key>(), applicationDTOInitial);

        final OnlineApplicationDTO applicationDTOupdated = EntityFactory.create(OnlineApplicationDTO.class);

        TestServiceFactory.create(ApplicationWizardService.class).init(new AsyncCallbackAssertion<OnlineApplicationDTO>() {

            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                applicationDTOupdated.set(result);

            }
        });

        assertEquals("firstName updated", newfirstName, applicationDTOupdated.applicant().person().name().firstName().getValue());
    }
}
