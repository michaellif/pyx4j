/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.generator;

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.generator.gdo.EquifaxIDecisionGenericTestCase;

public class TenantsEquifaxTestCasesGenerator {

    private final CustomerGenerator customerGenerator;

    private final ScreeningGenerator screeningGenerator;

    private static List<EquifaxIDecisionGenericTestCase> tenantInfoTestCases;

    private int curentTestCase = 0;

    public TenantsEquifaxTestCasesGenerator() {
        customerGenerator = new CustomerGenerator();
        screeningGenerator = new ScreeningGenerator();

        if (tenantInfoTestCases == null) {
            tenantInfoTestCases = EntityCSVReciver.create(EquifaxIDecisionGenericTestCase.class).loadResourceFile(
                    IOUtils.resourceFileName("iDecision-GenericTestCases.xlsx", TenantsEquifaxTestCasesGenerator.class));
            if (tenantInfoTestCases.size() == 0) {
                throw new Error("resourceFile iDecision-GenericTestCases.xlsx is empty");
            }
        }
    }

    private EquifaxIDecisionGenericTestCase getNextTenantInfo() {
        if (tenantInfoTestCases.size() > curentTestCase) {
            return tenantInfoTestCases.get(curentTestCase++);
        } else {
            return null;
        }
    }

    public boolean addTenants(Lease lease) {
        EquifaxIDecisionGenericTestCase tenantInfo = getNextTenantInfo();
        if (tenantInfo == null) {
            return false;
        } else {
            LeaseTermTenant mainTenant = EntityFactory.create(LeaseTermTenant.class);

            //mainTenant.leaseParticipant().customer().set(customerGenerator.createCustomer());
            mainTenant.leaseParticipant().customer().person().name().firstName().set(tenantInfo.firstName());
            mainTenant.leaseParticipant().customer().person().name().lastName().set(tenantInfo.lastName());
            mainTenant.leaseParticipant().customer().person().birthDate().set(tenantInfo.birthDate());

            mainTenant.leaseParticipant().customer().personScreening().set(screeningGenerator.createScreening());
            PriorAddress currentAddress = mainTenant.leaseParticipant().customer().personScreening().version().currentAddress();
            currentAddress.streetNumber().set(tenantInfo.streetNumber());
            currentAddress.streetName().set(tenantInfo.streetName());

            currentAddress.suiteNumber().setValue(null);
            currentAddress.streetDirection().setValue(null);
            currentAddress.streetType().setValue(StreetType.other);

            currentAddress.city().set(tenantInfo.city());
            currentAddress.province().code().set(tenantInfo.province());
            currentAddress.postalCode().set(tenantInfo.postalCode());

            currentAddress.moveInDate().setValue(new LogicalDate(2008 - 1900, 1, 1));
            currentAddress.moveOutDate().setValue(null);

            mainTenant.leaseParticipant().customer().personScreening().version().previousAddress().clearValues();

            mainTenant.leaseParticipant().customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());

            mainTenant.role().setValue(LeaseTermParticipant.Role.Applicant);
            lease.currentTerm().version().tenants().add(mainTenant);

            return true;
        }
    }

}
