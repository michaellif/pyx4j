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
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;
import com.propertyvista.pmsite.server.pages.MyCommunityPage;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

@SuppressWarnings("serial")
public class AuthenticationPanel extends Panel {

    private static final I18n i18n = I18n.get(AuthenticationPanel.class);

    public AuthenticationPanel(String id) {
        super(id);

        StatelessLink<Void> auth = new StatelessLink<Void>("authAction") {

            @Override
            public void onClick() {
                if (Context.isUserLoggedIn()) {
                    Lifecycle.endSession();
                    setResponsePage(getPage().getClass(), getPage().getPageParameters());
                } else {
                    setResponsePage(MyCommunityPage.class, null);
                }
            }
        };

        PageLink greet = new PageLink("greeting", MyCommunityPage.class);
        greet.setAnchor(AppPlaceInfo.getPlaceId(PortalSiteMap.PasswordChange.class));

        if (Context.isUserLoggedIn()) {
            greet.setText(i18n.tr("Welcome {0}", Context.getVisit().getUserVisit().getName()));
            auth.setBody(new Model<String>(i18n.tr("Log Out")));
        } else {
            greet.setVisible(false);
            auth.setBody(new Model<String>(i18n.tr("Log In")));
        }

        add(auth);
        add(greet);
    }
}