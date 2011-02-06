/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client.app.dashboard;

import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.widgets.client.GroupBoxPanel;

public class DashboardWidget extends VerticalPanel implements InlineWidget {

    public DashboardWidget() {

        setWidth("100%");
        getElement().getStyle().setPaddingLeft(30, Unit.PX);
        getElement().getStyle().setPaddingRight(200, Unit.PX);

        GroupBoxPanel generalGroup = new GroupBoxPanel(false);
        generalGroup.setCaption("General");
        //generalGroup.setContainer(general = new DashboardGeneralPanel());
        add(generalGroup);

        GroupBoxPanel analiticsGroup = new GroupBoxPanel(false);
        analiticsGroup.setCaption("Analitics");
        //analiticsGroup.setContainer(new DashboardAnaliticsPanel());
        add(analiticsGroup);

    }

    @Override
    public void populate(Map<String, String> args) {
    }
}
