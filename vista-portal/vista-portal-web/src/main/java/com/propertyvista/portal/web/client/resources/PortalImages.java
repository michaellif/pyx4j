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
package com.propertyvista.portal.web.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.images.IconButtonImages;

public interface PortalImages extends ClientBundle, EntityFolderImages, WidgetsImageBundle {

    PortalImages INSTANCE = GWT.create(PortalImages.class);

    // ================= Notification images ================= 
    @Override
    @Source("Messages_Icon_Success.png")
    ImageResource confirm();

    @Override
    @Source("Messages_Icon_Error.png")
    ImageResource error();

    @Override
    @Source("Messages_Icon_Information.png")
    ImageResource info();

    @Override
    @Source("Messages_Icon_Warning.png")
    ImageResource warning();

    // ==================================  

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("add.png")
    ImageResource addRow();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("del.png")
    ImageResource delRow();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("add_hover.png")
    ImageResource addRowHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("del_hover.png")
    ImageResource delRowHover();

    @ImageOptions(repeatStyle = RepeatStyle.None)
    @Source("noImage.jpg")
    ImageResource noImage();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pointer_menu.png")
    ImageResource pointer();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("menu.png")
    ImageResource menu();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("brand.png")
    ImageResource brand();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("profile_l.png")
    ImageResource avatar();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("alert.png")
    ImageResource alert();

    @Source("communication-message.png")
    ImageResource communicationMessage();

    @Source("communication-alert.png")
    ImageResource communicationAlert();

    @Source("communication-maintanance.png")
    ImageResource communicationMaintanancee();

    DashboardMenuImages dashboardMenu();

    interface DashboardMenuImages extends IconButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Dashboard-Inactive.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Dashboard-Inactive.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Dashboard-Active.png")
        ImageResource active();
    }

    BillingMenuImages billingMenu();

    interface BillingMenuImages extends IconButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_BillingPayments-Inactive.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_BillingPayments-Inactive.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_BillingPayments-Active.png")
        ImageResource active();
    }

}
