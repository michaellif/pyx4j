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
package com.propertyvista.crm.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CrmImages extends ClientBundle {

    CrmImages INSTANCE = GWT.create(CrmImages.class);

    @Source("bg_body.gif")
    ImageResource bodyBackground();

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

    @Source("DashboardAddGadget.png")
    ImageResource dashboardAddGadget();

    @Source("DashboardAddGadgetHover.png")
    ImageResource dashboardAddGadgetHover();

    //---New Images

    @Source("Bookmark_active.png")
    ImageResource Bookmark_active();

    @Source("Bookmark_hover.png")
    ImageResource Bookmark_hover();

    @Source("Bookmark_normal.png")
    ImageResource Bookmark_normal();

    @Source("Dashboards_active.png")
    ImageResource Dashboards_active();

    @Source("Dashboards_hover.png")
    ImageResource Dashboards_hover();

    @Source("Dashboards_normal.png")
    ImageResource Dashboards_normal();

    @Source("Feedback_hover.png")
    ImageResource Feedback_hover();

    @Source("Feedback_normal.png")
    ImageResource Feedback_normal();

    @Source("Finance_active.png")
    ImageResource Finance_active();

    @Source("Finance_hover.png")
    ImageResource Finance_hover();

    @Source("Finance_normal.png")
    ImageResource Finance_normal();

    @Source("Legal_active.png")
    ImageResource Legal_active();

    @Source("Legal_hover.png")
    ImageResource Legal_hover();

    @Source("Legal_normal.png")
    ImageResource Legal_normal();

    @Source("Marketing_active.png")
    ImageResource Marketing_active();

    @Source("Marketing_hover.png")
    ImageResource Marketing_hover();

    @Source("Marketing_normal.png")
    ImageResource Marketing_normal();

    @Source("Messages-Normal.png")
    ImageResource Messages_Normal();

    @Source("Messages_hover.png")
    ImageResource Messages_hover();

    @Source("Notifications_hover.png")
    ImageResource Notifications_hover();

    @Source("Notifications_normal.png")
    ImageResource Notifications_normal();

    @Source("Options_hover.png")
    ImageResource Options_hover();

    @Source("Options_normal.png")
    ImageResource Options_normal();

    @Source("Optoins2-hover.png")
    ImageResource Optoins2_hover();

    @Source("Optoins2-normal.png")
    ImageResource Optoins2_normal();

    @Source("Properties_active.png")
    ImageResource Properties_active();

    @Source("Properties_hover.png")
    ImageResource Properties_hover();

    @Source("Properties_normal.png")
    ImageResource Properties_normal();

    @Source("Reports_active.png")
    ImageResource Reports_active();

    @Source("Reports_hover.png")
    ImageResource Reports_hover();

    @Source("Reports_normal.png")
    ImageResource Reports_normal();

}
