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
package com.propertyvista.portal.admin.client.app;

import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.domain.SiteImpl;
import com.pyx4j.site.shared.meta.NavigUtils;

import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicResources;

public class VistaAdminAppSiteFactory extends com.pyx4j.site.shared.meta.SiteFactory {

    public Site createAppSite() {
        Site site = createSite(NavigUtils.getSiteId(VistaAdminAppSiteMap.App.class), "VistaAdmin");

        ((SiteImpl) site).setLogoUrl(VistaAdminPublicResources.INSTANCE.logo().getURL());

        site.pages().add(createSingleWidgetPage("Dashboard", VistaAdminAppSiteMap.App.Dashboard.class, VistaAdminAppWidgets.app$dashboardWidget));

        if (ClientSecurityController.checkBehavior(VistaBehavior.ADMIN)) {
            site.pages().add(createSingleWidgetPage("Users", VistaAdminAppSiteMap.App.Users.class, VistaAdminAppWidgets.app$userListWidget));
            site.pages().add(createSingleWidgetPage("Edit User", VistaAdminAppSiteMap.App.Users.Edit.class, VistaAdminAppWidgets.app$userEditorWidget));
        }
        return site;
    }

    @Override
    protected String footerCopyright() {
        return "&copy; 2011 Property Vista Software Inc. rights reserved.";
    }

}
