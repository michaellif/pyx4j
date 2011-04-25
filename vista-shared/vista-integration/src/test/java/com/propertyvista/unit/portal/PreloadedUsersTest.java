/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2011
 * @author vadym
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.unit.VistaBaseSeleniumTestCase;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class PreloadedUsersTest extends VistaBaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.portal);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
    }

    public void testFullFlow() throws Exception {
        VistaDataGenerator generator = new VistaDataGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));

        doTestAptPage(summary);
        doTestTenantsPage(summary);
        doTestInfoPage(summary);

    }

    protected void doTestAptPage(Summary summary) {
        return;
    }

    protected void doTestTenantsPage(Summary summary) {
        return;
    }

    protected void doTestInfoPage(Summary summary) {
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Info.class)));
        selenium.click(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Info.class));

        PotentialTenantInfo tenant = (PotentialTenantInfo) summary.tenantList().tenants().get(0).cloneEntity();

        assertValueOnForm(tenant.firstName());
        assertValueOnForm(tenant.lastName());
        assertValueOnForm(tenant.middleName());
        assertValueOnForm(tenant.email());
        assertValueOnForm(tenant.homePhone());
        assertValueOnForm(tenant.mobilePhone());
        assertValueOnForm(tenant.driversLicense());
        assertValueOnForm(tenant.secureIdentifier());
        //assertValueOnForm(tenant.notCanadianCitizen()); //does not work anymore
        //assertValueOnForm(tenant.driversLicenseState()); // doesn't work 

        assertAddressForm(tenant.currentAddress().getPath(), (Address) tenant.currentAddress().cloneEntity());
        assertAddressForm(tenant.previousAddress().getPath(), (Address) tenant.previousAddress().cloneEntity());

        //assertEqualsOnForm(tenant.legalQuestions().everEvicted());
        //...

        //TODO: 
        //Vehicles
        int num = 0;
        for (Vehicle vehicle : tenant.vehicles()) {
            // No need for cloneEntity() since elements in list are detached, I think..
            assertVehiclesForm(new CompositeDebugId(tenant.vehicles().getPath(), "row", num), vehicle);
            ////assertVehiclesForm(D.id(tenant.vehicles().getPath().debugId(), num), vehicle);
            num++;
        }

        //Legal Questions
        //Emergency Contacts

        return;
    }

    private void assertAddressForm(IDebugId fromDebugId, Address address) {
        assertValueOnForm(fromDebugId, address.street1());
        assertValueOnForm(fromDebugId, address.street2());
        assertValueOnForm(fromDebugId, address.city());
        assertValueOnForm(fromDebugId, address.phone());
        assertValueOnForm(fromDebugId, address.postalCode());
        assertValueOnForm(fromDebugId, address.moveInDate());
        assertValueOnForm(fromDebugId, address.moveOutDate());

        //TODO
        //assertValueOnForm(fromDebugId, address.rented()); //PotentialTenantInfo$currentAddress$rented instead of PotentialTenantInfo$currentAddress-Address$rented_Rented-input
        //assertValueOnForm(fromDebugId, address.country());
        //assertValueOnForm(tenant.currentAddress().province());

    }

    private void assertVehiclesForm(CompositeDebugId fromDebugId, Vehicle vehicle) {
        //TODO:
        //assertValueOnForm(fromDebugId, vehicle.make());
        //assertValueOnForm(fromDebugId, vehicle.model());

    }

    //    private void assertEqualsOnForm(IPrimitive<?> member) {
    private void assertValueOnForm(IPrimitive<?> member) {
        assertValueOnForm(null, member);
    }

    private void assertValueOnForm(IDebugId fromDebugId, IPrimitive<?> member) {
        // All your existing code for data types...
        assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(fromDebugId, member));
    }

}
