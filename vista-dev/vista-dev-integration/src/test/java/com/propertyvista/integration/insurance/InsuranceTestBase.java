/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.insurance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.AgreementLegalPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.TenantSureMerchantAccountDataModel;

public class InsuranceTestBase extends IntegrationTestBase {

    private Building building;

    private Lease lease;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerFacadeMock(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
        CreditCardPaymentProcessorFacadeMock.init();

        setTenantSureBatchProcess();
    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(AgreementLegalPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(LeaseDataModel.class);

        models.add(TenantSureMerchantAccountDataModel.class);

        return models;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
            Persistence.service().commit();
        }
        return building;
    }

    protected Lease getLease() {
        return lease;
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo) {
        lease = getDataModel(LeaseDataModel.class).addLease(getBuilding(), leaseDateFrom, leaseDateTo, BigDecimal.TEN, null,
                Arrays.asList(new Customer[] { getDataModel(CustomerDataModel.class).addCustomer() }));
    }

    public static InsurancePaymentMethod createInsurancePaymentMethod(Tenant tenant) {
        InsurancePaymentMethod paymentMethod = EntityFactory.create(InsurancePaymentMethod.class);
        paymentMethod.tenant().set(tenant);
        paymentMethod.type().setValue(PaymentType.CreditCard);

        CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
        details.cardType().setValue(CreditCardType.Visa);
        details.card().newNumber().setValue(CreditCardNumberGenerator.generateCardNumber(details.cardType().getValue()));
        details.expiryDate().setValue(new LogicalDate(2015 - 1900, 1, 1));
        paymentMethod.details().set(details);

        return paymentMethod;
    }
}
