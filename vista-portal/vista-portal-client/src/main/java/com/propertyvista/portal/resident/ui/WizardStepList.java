/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ui.WizardStepItem.StepStatus;
import com.propertyvista.portal.shared.ui.MenuList;

public class WizardStepList extends MenuList<WizardStepItem> {

    public WizardStepList() {

    }

    public void addStepItem(String stepTitle, final int stepIndex, StepStatus stepStatus) {
        WizardStepItem menuItem = new WizardStepItem(stepTitle, new Command() {
            @Override
            public void execute() {
                System.out.println("++++++++++++++++++++++execute");
                LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                if (LayoutType.phonePortrait.equals(layout) || (LayoutType.phoneLandscape.equals(layout))) {
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                }

            }
        }, stepIndex, stepStatus);
        addMenuItem(menuItem);
    }

}