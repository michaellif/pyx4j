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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.EmptyAsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.ProspectSignUpDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AgreementLegalPolicyDataModel;
import com.propertyvista.test.mock.models.AutoPayPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseApplicationDocumentationPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseApplicationLegalPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseApplicationProspectPortalPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseApplicationRestrictionsPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.MerchantAccountDataModel;
import com.propertyvista.test.mock.models.PaymentTypeSelectionPolicyDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.ProductTaxPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;

public abstract class ProspectInternalTestBase extends IntegrationTestBase {

    private Building building;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.setNamespace();
        super.tearDown();
    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(PaymentTypeSelectionPolicyDataModel.class);
        models.add(LeaseApplicationRestrictionsPolicyDataModel.class);
        models.add(LeaseApplicationDocumentationPolicyDataModel.class);
        models.add(LeaseApplicationLegalPolicyDataModel.class);
        models.add(LeaseApplicationProspectPortalPolicyDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(MerchantAccountDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(ProductTaxPolicyDataModel.class);
        models.add(DepositPolicyDataModel.class);
        models.add(LeaseAdjustmentPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        models.add(AutoPayPolicyDataModel.class);
        models.add(AgreementLegalPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
            getDataModel(MerchantAccountDataModel.class).addMerchantAccount(building);
            Persistence.service().commit();
        }
        return building;
    }

    OnlineApplicationDTO createApplication() {
        ProspectSignUpDTO signUp = EntityFactory.create(ProspectSignUpDTO.class);

        signUp.firstName().setValue(DataGenerator.randomFirstName());
        signUp.lastName().setValue(DataGenerator.randomLastName());
        signUp.email().setValue("t001@pyx4j.com");
        signUp.password().setValue("pwd");

        signUp.ilsBuildingId().setValue(getBuilding().propertyCode().getValue());

        TestServiceFactory.create(ProspectSignUpService.class).signUp(new EmptyAsyncCallbackAssertion<VoidSerializable>(), signUp);

        TestServiceFactory.create(ProspectAuthenticationService.class).authenticate(new EmptyAsyncCallbackAssertion<AuthenticationResponse>(),
                new ClientSystemInfo(), signUp);

        final OnlineApplicationDTO applicationDTO = EntityFactory.create(OnlineApplicationDTO.class);

        // Now we have working session
        TestServiceFactory.create(ApplicationWizardService.class).init(new AsyncCallbackAssertion<OnlineApplicationDTO>() {

            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                applicationDTO.set(result);

            }
        });

        return applicationDTO;
    }

}
