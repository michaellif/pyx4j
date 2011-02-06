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
package com.propertyvista.portal.admin.client.site;

import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.PageDataImpl;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.domain.SiteImpl;
import com.pyx4j.site.shared.meta.NavigUtils;

public class VistaAdminPublicSiteFactory extends com.pyx4j.site.shared.meta.SiteFactory {

    public Site createPubSite() {
        Site site = createSite(NavigUtils.getSiteId(VistaAdminPublicSiteMap.Pub.class), "Vista");

        ((SiteImpl) site).setLogoUrl(VistaAdminPublicResources.INSTANCE.logo().getURL());

        Page pageLanding = createPage("Welcome", VistaAdminPublicSiteMap.Pub.Home.Landing.class, null, VistaAdminPublicResources.INSTANCE.sitePageLanding()
                .getText());
        ((PageDataImpl) pageLanding.data()).addInlineWidgetId(VistaAdminPublicSiteMap.Widgets.AcceptDeclineWidget.name());
        site.pages().add(pageLanding);

        site.pages().add(createSingleWidgetPage("Signing out", VistaAdminPublicSiteMap.Pub.Home.SignOut.class, VistaAdminPublicSiteMap.Widgets.SignOutWidget));

        return site;
    }

    @Override
    protected String footerCopyright() {
        return "&copy; 2011 Property Vista Software Inc. All rights reserved.";
    }
}
