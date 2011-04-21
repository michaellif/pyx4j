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

    @Source("DashboardLayout10.png")
    ImageResource dashboardLayout10();

    @Source("DashboardLayout11.png")
    ImageResource dashboardLayout11();

    @Source("DashboardLayout20.png")
    ImageResource dashboardLayout20();

    @Source("DashboardLayout21.png")
    ImageResource dashboardLayout21();

    @Source("DashboardLayout30.png")
    ImageResource dashboardLayout30();

    @Source("DashboardLayout31.png")
    ImageResource dashboardLayout31();

    @Source("DashboardAddGadget.png")
    ImageResource dashboardAddGadget();

    @Source("DashboardAddGadgetHover.png")
    ImageResource dashboardAddGadgetHover();

}
