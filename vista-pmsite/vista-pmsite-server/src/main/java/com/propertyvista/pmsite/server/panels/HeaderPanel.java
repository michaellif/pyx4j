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

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.pmsite.server.pages.LandingPage;

public class HeaderPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public HeaderPanel() {
        super("header");

        add(new BookmarkablePageLink<Void>("titleLogo", LandingPage.class));

        add(new LocalePanel("locale"));

        add(new AuthenticationPanel("auth"));

        add(new MainNavigationPanel("mainNavig"));
    }

}