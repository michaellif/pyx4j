/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.admin.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AdminImages extends ClientBundle {

    AdminImages INSTANCE = GWT.create(AdminImages.class);

    @Source("user_message_info.png")
    ImageResource userMessageInfo();

    @Source("logo.png")
    ImageResource logo();

    @Source("bell.png")
    ImageResource alert();

    @Source("envelop.png")
    ImageResource message();

    @Source("blank.gif")
    ImageResource blank();

    @Source("search.png")
    ImageResource search();

    @Source("add.png")
    ImageResource add();

    @Source("add_hover.png")
    ImageResource addHover();

    @Source("del.png")
    ImageResource del();

    @Source("del_hover.png")
    ImageResource delHover();

    @Source("Feedback_hover.png")
    ImageResource feedbackHover();

    @Source("Feedback_normal.png")
    ImageResource feedbackNormal();

    @Source("Options_hover.png")
    ImageResource optionsHover();

    @Source("Options_normal.png")
    ImageResource optionsNormal();

    @Source("Optoins2-hover.png")
    ImageResource optoins2Hover();

    @Source("Optoins2-normal.png")
    ImageResource optoins2Normal();

    @Source("DashboardAddGadget.png")
    ImageResource dashboardAddGadget();

    @Source("DashboardAddGadgetHover.png")
    ImageResource dashboardAddGadgetHover();

    @Source("DashboardLayout1-0.png")
    ImageResource dashboardLayout1_0();

    @Source("DashboardLayout1-1.png")
    ImageResource dashboardLayout1_1();

    @Source("DashboardLayout12-0.png")
    ImageResource dashboardLayout12_0();

    @Source("DashboardLayout12-1.png")
    ImageResource dashboardLayout12_1();

    @Source("DashboardLayout21-0.png")
    ImageResource dashboardLayout21_0();

    @Source("DashboardLayout21-1.png")
    ImageResource dashboardLayout21_1();

    @Source("DashboardLayout22-0.png")
    ImageResource dashboardLayout22_0();

    @Source("DashboardLayout22-1.png")
    ImageResource dashboardLayout22_1();

    @Source("DashboardLayout3-0.png")
    ImageResource dashboardLayout3_0();

    @Source("DashboardLayout3-1.png")
    ImageResource dashboardLayout3_1();

    @Source("Dashboards_active.png")
    ImageResource dashboardsActive();

    @Source("Dashboards_hover.png")
    ImageResource dashboardsHover();

    @Source("Dashboards_normal.png")
    ImageResource dashboardsNormal();
}
