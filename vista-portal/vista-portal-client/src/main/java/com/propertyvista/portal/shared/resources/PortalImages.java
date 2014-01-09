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
package com.propertyvista.portal.shared.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;
import com.pyx4j.widgets.client.images.ButtonImages;

public interface PortalImages extends ClientBundle, EntityFolderImages, WidgetsImageBundle {

    PortalImages INSTANCE = GWT.create(PortalImages.class);

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
    @Source("Header_Logo-myCommunity.png")
    ImageResource myCommunityHeaderLogo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Header_Logo-myCommunityLabel.png")
    ImageResource myCommunityHeaderLogoLabel();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Footer_Logo-myCommunity.png")
    ImageResource myCommunityFooterLogo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Footer_Logo-PropertyVista.png")
    ImageResource vistaFooterLogo();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("WriteMessage.png")
    ImageResource writeMessage();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Nav_Icon_Profile-Active.png")
    ImageResource avatar();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Nav_Icon_Profile-Inactive.png")
    ImageResource avatar2();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("avatar.png")
    ImageResource avatar3();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Header_Icon_Notifications-New.png")
    ImageResource alert();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("brushed_alu.png")
    ImageResource background();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Dashboard-LocationIcon.png")
    ImageResource marker();

    //=============== SignUp ====================

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("SafeAndSecure.png")
    ImageResource safeAndSecure();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("SafeAndSecureS.png")
    ImageResource safeAndSecureS();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("EasyToUse.png")
    ImageResource easyToUse();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("EasyToUseS.png")
    ImageResource easyToUseS();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("ManageRequests.png")
    ImageResource manageRequests();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("ManageRequestsS.png")
    ImageResource manageRequestsS();

    //=============== Social ====================

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Social-Facebook.png")
    ImageResource socialFacebook();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Social-Twitter.png")
    ImageResource socialTwitter();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Social-YouTube.png")
    ImageResource socialYouTube();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Social-Flickr.png")
    ImageResource socialFlickr();

    //=============== Devices ====================

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Huge.gif")
    ImageResource huge();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Monitor.gif")
    ImageResource monitor();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("TabletL.gif")
    ImageResource tabletL();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Tablet.gif")
    ImageResource tablet();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("PhoneL.gif")
    ImageResource phoneL();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Phone.gif")
    ImageResource phone();

    //=============== Devices ====================

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("SignUp-Building.png")
    ImageResource signUpBuilding();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("SignUp-Personal.png")
    ImageResource signUpPersonal();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("SignUp-Security.png")
    ImageResource signUpSecurity();

    //=============== Menu ====================

    DashboardMenuImages dashboardMenu();

    interface DashboardMenuImages extends ButtonImages {
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

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("BillingPayments-Green.png")
    ImageResource billingIcon();

    BillingMenuImages billingMenu();

    interface BillingMenuImages extends ButtonImages {
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

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Maintenance-Blue.png")
    ImageResource maintenanceIcon();

    MaintenanceMenuImages maintenanceMenu();

    interface MaintenanceMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Maintenance-Inactive.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Maintenance-Inactive.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Maintenance-Active.png")
        ImageResource active();
    }

    ProfileMenuImages profileMenu();

    interface ProfileMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Profile-Active.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Profile-Active.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Profile-Inactive.png")
        ImageResource active();
    }

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("ResidentServices-Red.png")
    ImageResource residentServicesIcon();

    ResidentServicesMenuImages residentServicesMenu();

    interface ResidentServicesMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_ResidentServices-Inactive.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_ResidentServices-Inactive.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_ResidentServices-Active.png")
        ImageResource active();
    }

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("Perks.png")
    ImageResource offersIcon();

    OffersMenuImages offersMenu();

    interface OffersMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Perks-Inactive.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Perks-Inactive.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Perks-Active.png")
        ImageResource active();
    }

    AccountMenuImages accountMenu();

    interface AccountMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Settings-Active.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Settings-Active.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Settings-Inactive.png")
        ImageResource active();
    }

    LogoutMenuImages logoutMenu();

    interface LogoutMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_Icon_Logout.png")
        ImageResource regular();
    }

    SelectMenuImages selectMenu();

    interface SelectMenuImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("Nav_ApplicationIcon.png")
        ImageResource regular();
    }

    EditIconButtonImages editButton();

    public interface EditIconButtonImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("edit.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("edit_hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("edit.png")
        ImageResource active();
    }

}
