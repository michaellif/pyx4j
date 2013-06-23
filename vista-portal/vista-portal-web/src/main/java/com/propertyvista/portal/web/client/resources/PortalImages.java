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
import com.pyx4j.widgets.client.images.IconButtonImages;

public interface PortalImages extends ClientBundle, EntityFolderImages {

    PortalImages INSTANCE = GWT.create(PortalImages.class);

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

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("map_marker.png")
    ImageResource mapMarker();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("map_marker_outbound.png")
    ImageResource mapMarkerOutbound();

    @ImageOptions(repeatStyle = RepeatStyle.None)
    @Source("noImage.jpg")
    ImageResource noImage();

    @ImageOptions(repeatStyle = RepeatStyle.None)
    @Source("map.png")
    ImageResource map();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("pointer_menu.png")
    ImageResource pointer();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("info_orange.png")
    ImageResource warning();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("info_side_orange.png")
    ImageResource warningSide();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("user_message_info.png")
    ImageResource userMessageInfo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("menu.png")
    ImageResource menu();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("brand.png")
    ImageResource brand();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("panda.gif")
    ImageResource avatar();

    @Source("communication-message.png")
    ImageResource communicationMessage();

    @Source("communication-alert.png")
    ImageResource communicationAlert();

    @Source("communication-maintanance.png")
    ImageResource communicationMaintanancee();

    DashboardIconButtonImages dashboardButton();

    public interface DashboardIconButtonImages extends IconButtonImages {
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
        ImageResource pushed();
    }

}
