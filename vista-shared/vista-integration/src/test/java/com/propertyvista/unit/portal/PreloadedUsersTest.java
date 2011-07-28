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

import com.propertvista.generator.PTGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.User;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.server.common.reference.SharedData;

public class PreloadedUsersTest extends PortalVerificationTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        // This is just to make the test more visual
        selenium.setFocusOnGetValue(true);
    }

    public void testFullFlow() throws Exception {
        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED, PreloadConfig.createTest());
        User user = generator.createUser(1);
        ApplicationSummaryGDO summary = generator.createSummary(user, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

//TODO - there is no unitSelection() in ApplicationSummaryDTO currently!...           
//        verifyAptPage(summary.unitSelection());

        verifyTenantsPage(summary.tenants(), true);
        verifyInfoPages(summary.tenants(), true);
        verifyFinancialPages(summary.tenants(), true);
        verifyPetsPages(summary.lease().pets(), false);

    }

    private void verifyAptPage(UnitSelection unitSel) {
        assertVisible(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));
        selenium.click(D.id(VistaFormsDebugId.MainNavigation_Prefix, PtSiteMap.Apartment.class));

        //verify all of them
        int num = 0;
        //TODO VLAD:: why this always returns empty list? How to do that properly? 
        int size2 = unitSel.availableUnits().units().size();
        for (AptUnitDTO aUnit : unitSel.availableUnits().units()) {
            assertAptUnitForm(D.id(unitSel.availableUnits().units(), num), detach(aUnit));
            num++;
        }

        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).unitType()));
        selenium.click(D.id(proto(UnitSelection.class).availableUnits().units(), 1, "leaseTerm_12"));
        String avlDate = selenium.getText(D.id(proto(UnitSelection.class).availableUnits().units(), 1, proto(AptUnitDTO.class).avalableForRent()));
        selenium.setValue(D.id(proto(UnitSelection.class).rentStart()), avlDate);
        saveAndContinue();
    }

}
