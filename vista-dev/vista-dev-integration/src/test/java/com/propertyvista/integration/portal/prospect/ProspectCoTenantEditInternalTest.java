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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.EmptyAsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class ProspectCoTenantEditInternalTest extends ProspectInternalTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        getBuilding();
        TestLifecycle.endRequest();
        // Logout from CRM
        TestLifecycle.testSession(null);
    }

    public void testCoTenantEdit() {

        final OnlineApplicationDTO applicationDTO = createApplication();

        // Add Co-Tenant
        CoapplicantDTO coapplicant = applicationDTO.coapplicants().$();
        coapplicant.name().firstName().setValue("CoopF " + DataGenerator.randomFirstName());
        coapplicant.name().lastName().setValue("CoopL " + DataGenerator.randomLastName());
        coapplicant.relationship().setValue(PersonRelationship.Aunt);
        coapplicant.email().setValue("cotenant009@aa.com");

        applicationDTO.coapplicants().add(coapplicant);

        TestServiceFactory.create(ApplicationWizardService.class).save(new EmptyAsyncCallbackAssertion<Key>(), applicationDTO);

        final OnlineApplicationDTO applicationDTOupdated1 = EntityFactory.create(OnlineApplicationDTO.class);

        TestServiceFactory.create(ApplicationWizardService.class).init(new AsyncCallbackAssertion<OnlineApplicationDTO>() {

            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                applicationDTOupdated1.set(result);

            }
        });

        assertEquals("saved CoApplicant", 1, applicationDTO.coapplicants().size());

        assertEquals("email saved", coapplicant.email(), applicationDTOupdated1.coapplicants().get(0).email());

        TestServiceFactory.create(ApplicationWizardService.class).save(new EmptyAsyncCallbackAssertion<Key>(), applicationDTOupdated1);

        final OnlineApplicationDTO applicationDTOupdated2 = EntityFactory.create(OnlineApplicationDTO.class);

        TestServiceFactory.create(ApplicationWizardService.class).init(new AsyncCallbackAssertion<OnlineApplicationDTO>() {

            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                applicationDTOupdated2.set(result);

            }
        });

        assertEquals("email saved again", coapplicant.email(), applicationDTOupdated2.coapplicants().get(0).email());
    }
}
