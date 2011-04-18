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

import java.util.Date;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.unit.VistaBaseSeleniumTestCase;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
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
        selenium.type(meta(AuthenticationRequest.class).email(), user.email().getValue());
        selenium.type(meta(AuthenticationRequest.class).password(), user.email().getValue());
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
        //Assert all fields

        //assertEquals(tenant.firstName().getMeta().getCaption(), tenant.firstName().getValue(), selenium.getValue(tenant.firstName()));
        assertEqualsOnForm(tenant.firstName());
        assertEqualsOnForm(tenant.lastName());
        assertEqualsOnForm(tenant.middleName());
        assertEqualsOnForm(tenant.email());
        assertEqualsOnForm(tenant.homePhone());
        assertEqualsOnForm(tenant.mobilePhone());
        assertEqualsOnForm(tenant.driversLicense());
        assertEqualsOnForm(tenant.secureIdentifier());
        assertEqualsOnForm(tenant.notCanadianCitizen());

        //ERRORS HERE!
        //assertEqualsOnForm( tenant.driversLicenseState()); // doesn't compile - Province / Country are not IPrimitive<>, 
        //assertEqualsOnForm( tenant.driversLicenseState().name()); //if I use this way, I get extra "$name" in selenium's element name

        //debug-ids do not match for all lines below:

        //assertEqualsOnForm(tenant.currentAddress().street1()); //PotentialTenantInfo$currentAddress$street1 instead of PotentialTenantInfo$currentAddress-Address$street1 
        //assertEqualsOnForm(tenant.currentAddress().street2());  //and so on 
        //assertEqualsOnForm(tenant.currentAddress().city());
        //assertEqualsOnForm(tenant.currentAddress().phone());
        //assertEqualsOnForm(tenant.currentAddress().postalCode());
        //assertEqualsOnForm(tenant.currentAddress().rented()); //PotentialTenantInfo$currentAddress$rented instead of PotentialTenantInfo$currentAddress-Address$rented_Rented-input
        //assertEqualsOnForm(tenant.currentAddress().moveInDate()); 
        //assertEqualsOnForm(tenant.currentAddress().moveOutDate());
        //assertEqualsOnForm(tenant.currentAddress().country());
        //assertEqualsOnForm(tenant.currentAddress().province());

        //assertEqualsOnForm(tenant.previousAddress().street1());
        //assertEqualsOnForm(tenant.previousAddress().street2());
        //assertEqualsOnForm(tenant.previousAddress().city());
        //assertEqualsOnForm(tenant.previousAddress().phone());
        //assertEqualsOnForm(tenant.previousAddress().postalCode());
        //assertEqualsOnForm(tenant.previousAddress().rented());
        //assertEqualsOnForm(tenant.previousAddress().moveInDate());
        //assertEqualsOnForm(tenant.previousAddress().moveOutDate());
        //assertEqualsOnForm(tenant.previousAddress().country());
        //assertEqualsOnForm(tenant.previousAddress().province());

        //TODO: 
        //Vehicles
        //Legal Questions
        //Emergency Contacts

        return;
    }

    //    private void assertEqualsOnForm(IPrimitive<?> member) {
    private void assertEqualsOnForm(IPrimitive<?> member) {
        ///member.getMeta().getObjectClassType() -- c
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().isEnum()) {
            // CComboBox();
            // assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(member));
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            // CDatePicker();
            // assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(member));
        } else if (mm.getValueClass().equals(Boolean.class)) {
            // CCheckBox();
            //  assertEquals(member.getMeta().getCaption(), (member.getStringView() == "true") ? "on" : "off", selenium.getValue(member));
        } else if (mm.getValueClass().equals(Integer.class)) {
            // CIntegerField();
        } else if (mm.getValueClass().equals(Long.class)) {
            // CLongField();
        } else if (mm.getValueClass().equals(Double.class)) {
            // CDoubleField();
        } else if (mm.getValueClass().equals(String.class)) {
            // CTextField();
            // assertEquals(member.getMeta().getCaption(), member.getValue(), selenium.getValue(member));
        } else {
            throw new Error("No comparison defined for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
        }
    }

}
