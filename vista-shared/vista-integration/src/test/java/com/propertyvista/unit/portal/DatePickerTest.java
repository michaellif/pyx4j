/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-18
 * @author yura
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.generator.VistaDataPTGenerator;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.NativeTriggerComponent;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.D;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class DatePickerTest extends WizardBaseSeleniumTestCase {

    private final IDebugId gridId = new StringDebugId(DatePickerComposite.DateGridDebugID.DateGrid_Index_.toString() + 0);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedData.init();
        // This is just to make the test more visual
        selenium.setFocusOnGetValue(true);
    }

    public void testDatePicker() throws Exception {
        VistaDataPTGenerator generator = new VistaDataPTGenerator(DemoData.PT_GENERATION_SEED);
        User user = generator.createUser(1);
        Application application = generator.createApplication(user);
        Summary summary = generator.createSummary(application, null);
        UnitSelection unitSel = generator.createUnitSelection(application, null);

        selenium.click(VistaFormsDebugId.Auth_Login);
        selenium.type(D.id(proto(AuthenticationRequest.class).email()), user.email().getValue());
        selenium.type(D.id(proto(AuthenticationRequest.class).password()), user.email().getValue());
        selenium.click(CrudDebugId.Criteria_Submit);
        assertVisible(CompositeDebugId.debugId(VistaFormsDebugId.MainNavigation_Prefix, AppPlaceInfo.getPlaceIDebugId(PtSiteMap.Apartment.class)));

        startRentDateTest();
    }

    private void startRentDateTest() {
        IDebugId from = new CompositeDebugId(D.id(proto(UnitSelection.class).selectionCriteria().availableFrom()),
                NativeTriggerComponent.NativeTriggerId.trigger.name());
        //selenium.click(from);
        //selenium.click(getCellDebugId(1, 1, gridId));
        //selenium.click(D.id(proto(UnitSelection.class).selectionCriteria().availableTo()));
    }

    private IDebugId getCellDebugId(int row, int column, IDebugId parent) {
        StringDebugId child = new StringDebugId(DatePickerComposite.DateCellDebugID.row_.name() + row + DatePickerComposite.DateCellDebugID._col_.name()
                + column);
        IDebugId debugId = new CompositeDebugId(parent, child);
        return debugId;
    }

}
