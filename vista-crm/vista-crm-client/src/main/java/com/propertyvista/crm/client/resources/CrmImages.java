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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.widgets.client.images.IconButtonImages;

import com.propertyvista.common.client.resources.VistaImages;

public interface CrmImages extends VistaImages {

    CrmImages INSTANCE = GWT.create(CrmImages.class);

    @Source("user_message_info.png")
    ImageResource userMessageInfo();

    @Source("bell.png")
    ImageResource alert();

    @Source("envelop.png")
    ImageResource message();

    @Source("blank.gif")
    ImageResource blank();

    @Source("search.png")
    ImageResource search();

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

    @Source("dashboard_print.png")
    ImageResource dashboardPrint();

    @Source("dashboard_print_hover.png")
    ImageResource dashboardPrintHover();

    // Accordion Menu Folder Images:

    @Source("Bookmark_active.png")
    ImageResource bookmarkActive();

    @Source("Bookmark_hover.png")
    ImageResource bookmarkHover();

    @Source("Bookmark_normal.png")
    ImageResource bookmarkNormal();

    @Source("Dashboards_active.png")
    ImageResource dashboardsActive();

    @Source("Dashboards_hover.png")
    ImageResource dashboardsHover();

    @Source("Dashboards_normal.png")
    ImageResource dashboardsNormal();

    @Source("Feedback_hover.png")
    ImageResource feedbackHover();

    @Source("Feedback_normal.png")
    ImageResource feedbackNormal();

    @Source("Finance_active.png")
    ImageResource financeActive();

    @Source("Finance_hover.png")
    ImageResource financeHover();

    @Source("Finance_normal.png")
    ImageResource financeNormal();

    @Source("Legal_active.png")
    ImageResource legalActive();

    @Source("Legal_hover.png")
    ImageResource legalHover();

    @Source("Legal_normal.png")
    ImageResource legalNormal();

    @Source("Marketing_active.png")
    ImageResource marketingActive();

    @Source("Marketing_hover.png")
    ImageResource marketingHover();

    @Source("Marketing_normal.png")
    ImageResource marketingNormal();

    @Source("Messages_normal.png")
    ImageResource messagesNormal();

    @Source("Messages_hover.png")
    ImageResource messagesHover();

    @Source("Notifications_hover.png")
    ImageResource notificationsHover();

    @Source("Notifications_normal.png")
    ImageResource notificationsNormal();

    @Source("Options_hover.png")
    ImageResource optionsHover();

    @Source("Options_normal.png")
    ImageResource optionsNormal();

    @Source("Optoins2-hover.png")
    ImageResource optoins2Hover();

    @Source("Optoins2-normal.png")
    ImageResource optoins2Normal();

    @Source("Properties_active.png")
    ImageResource propertiesActive();

    @Source("Properties_hover.png")
    ImageResource propertiesHover();

    @Source("Properties_normal.png")
    ImageResource propertiesNormal();

    @Source("Units_normal.png")
    ImageResource unitsNormal();

    @Source("Reports_active.png")
    ImageResource reportsActive();

    @Source("Reports_hover.png")
    ImageResource reportsHover();

    @Source("Reports_normal.png")
    ImageResource reportsNormal();

    @Source("Tenants_active.png")
    ImageResource tenantsActive();

    @Source("Tenants_hover.png")
    ImageResource tenantsHover();

    @Source("Tenants_normal.png")
    ImageResource tenantsNormal();

    @Source("Tenant.png")
    ImageResource tenant();

    @Source("Company_active.png")
    ImageResource companyActive();

    @Source("Company_hover.png")
    ImageResource companyHover();

    @Source("Company_normal.png")
    ImageResource companyNormal();

    @Source("Notice_warning.png")
    ImageResource noticeWarning();

    EditIconButtonImages editButton();

    public interface EditIconButtonImages extends IconButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Dashboards_normal.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Dashboards_hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Dashboards_active.png")
        ImageResource active();
    }
}
