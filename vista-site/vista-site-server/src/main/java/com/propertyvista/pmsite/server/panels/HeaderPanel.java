/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.server.admin.SystemMaintenance;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;
import com.propertyvista.pmsite.server.model.WicketUtils.ResourceImage;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.MyCommunityPage;

public class HeaderPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public HeaderPanel(boolean residentsOnly) {
        super("header");

        PageLink logoLink = new PageLink("logoLink", LandingPage.class);
        SimpleImage logoImg;
        Label noLogo = new Label("noLogoLabel", "");
        // logo
        PMSiteContentManager cm = ((PMSiteWebRequest) getRequest()).getContentManager();
        AvailableLocale locale = ((PMSiteWebRequest) getRequest()).getSiteLocale();
        PortalLogoImageResource logo = cm.getSiteLogo(locale);
        if (logo == null) {
            logoImg = new SimpleImage("logoImg", "");
            logoImg.setVisible(false);
            noLogo.setDefaultModelObject(cm.getSiteTitles(locale).residentPortalTitle().getStringView());
        } else {
            logoImg = new ResourceImage("logoImg", logo.large());
            noLogo.setVisible(false);
        }
        logoLink.add(logoImg);
        logoLink.add(noLogo);
        add(logoLink);
        // slogan
        add(new Label("slogan", cm.getSiteSlogan(locale)).setEscapeModelStrings(false));
        // show login if Residents page is enabled
        boolean residentsEnabled = false;
        try {
            residentsEnabled = cm.getSiteDescriptor().residentPortalSettings().enabled().isBooleanTrue()
                    && SystemState.Online.equals(SystemMaintenance.getState());
        } catch (Exception ignore) {
        }
        add(residentsEnabled ? new PageLink("auth", MyCommunityPage.class) : new Label("auth"));
        // main menu
        if (!residentsOnly) {
            add(new MainNavigationPanel("mainNavig"));
        } else {
            add(new Label("mainNavig").setVisible(false));
        }
    }
}