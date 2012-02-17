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

import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.PwdChangePage;
import com.propertyvista.pmsite.server.pages.SignInPage;

public class AuthenticationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AuthenticationPanel.class);

    public AuthenticationPanel(String id) {
        super(id);

        StatelessLink<Void> auth = new StatelessLink<Void>("authAction") {
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

        StatelessLink<Void> greet = new StatelessLink<Void>("greeting") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(PwdChangePage.class, null);
            }
        };

        if (((PMSiteSession) getSession()).isSignedIn()) {
            greet.setBody(new Model<String>(i18n.tr("Welcome {0}", Context.getVisit().getUserVisit().getName())));
            auth.setBody(new Model<String>(i18n.tr("LogOut")));
        } else {
            greet.setVisible(false);
            auth.setBody(new Model<String>(i18n.tr("LogIn")));
        }

        add(auth);
        add(greet);
    }
}