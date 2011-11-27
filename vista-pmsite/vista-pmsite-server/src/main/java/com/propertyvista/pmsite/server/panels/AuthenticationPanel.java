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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.SignInPage;

public class AuthenticationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static I18n i18n = I18n.get(AuthenticationPanel.class);

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

        if (((PMSiteSession) getSession()).isSignedIn()) {
            this.add(new Label("greetings", i18n.tr("Welcome {0}", Context.getVisit().getUserVisit().getName())));
            link.add(new Label("caption", i18n.tr("LogOut")));
        } else {
            this.add(new Label("greetings", ""));
            link.add(new Label("caption", i18n.tr("Login")));
        }

    }
}