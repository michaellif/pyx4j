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
package com.propertyvista.portal.admin.client;

import com.google.gwt.user.client.Command;
import com.propertyvista.portal.admin.client.site.VistaAdminPublicSiteMap;

import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.CommandLink;
import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.SkinFactory;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.widgets.client.style.Theme;

public abstract class VistaAdminBaseSitePanel extends SitePanel {

    private CommandLink logOutLink;

    private final VistaAdminResources bundle;

    public VistaAdminBaseSitePanel(Site site, VistaAdminResources bundle) {
        super(site, bundle);
        this.bundle = bundle;
        setSkinFactory(new SkinFactory() {
            @Override
            public Theme createSkin(String skinName) {
                return new VistaAdminTheme();
            }
        });
    }

    protected CommandLink getLogOutLink() {
        if (logOutLink == null) {
            logOutLink = new CommandLink("Sign Out", new Command() {
                @Override
                public void execute() {
                    AbstractSiteDispatcher.show(VistaAdminPublicSiteMap.Pub.Home.SignOut.class);
                }
            });
        }
        return logOutLink;
    }

}
