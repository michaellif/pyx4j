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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataGenerator;

public class PreloadedUsersTest extends PortalVerificationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        // This is just to make the test more visual
        selenium.setFocusOnGetValue(true);
    }

    public void testFullFlow() throws Exception {
        VistaDataGenerator generator = new VistaDataGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);
        UnitSelection unitSel = generator.createUnitSelection(application, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(SiteMap.Apartment.class)));

        verifyAptPage(unitSel);
        verifyTenantsPage(summary, true);
        verifyInfoPages(summary, true);
        verifyFinancialPages(summary, true);
        verifyPetsPages(summary, false);

    }

    private void verifyAptPage(UnitSelection unitSel) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Apartment.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, SiteMap.Apartment.class));

        //verify all of them
        int num = 0;
        //TODO VLAD:: why this always returns empty list? How to do that properly? 
        int size2 = unitSel.availableUnits().units().size();
        for (ApartmentUnit aUnit : unitSel.availableUnits().units()) {
            assertAptUnitForm(D.id(unitSel.availableUnits().units(), num), detach(aUnit));
            num++;
        }

        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(ApartmentUnit.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));
        String avlDate = selenium.getText(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(ApartmentUnit.class).avalableForRent()));
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), avlDate);
        saveAndContinue();
    }

}
