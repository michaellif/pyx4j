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
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.SignInPage;

public class AuthenticationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public AuthenticationPanel(String id) {
        super(id);

        StatelessLink<Void> link = new StatelessLink<Void>("authAction") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                if (((PMSiteSession) getSession()).isSignedIn()) {
                    getSession().invalidate();
                    setResponsePage(getPage().getClass(), getPage().getPageParameters());
                } else {
                    setResponsePage(SignInPage.class, null);
                }
            }
        };

        add(link);

        link.add(new Label("caption", ((PMSiteSession) getSession()).isSignedIn() ? "Logout" : "Login"));

    }
}