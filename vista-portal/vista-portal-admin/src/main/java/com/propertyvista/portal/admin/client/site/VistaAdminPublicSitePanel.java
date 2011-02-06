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

import com.propertyvista.portal.admin.client.VistaAdminBaseSitePanel;

import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;
import com.pyx4j.site.shared.domain.Site;

public class VistaAdminPublicSitePanel extends VistaAdminBaseSitePanel implements InlineWidgetFactory {

    public VistaAdminPublicSitePanel(Site site) {
        super(site, VistaAdminPublicResources.INSTANCE);
    }

    @Override
    public InlineWidgetFactory getLocalWidgetFactory() {
        return this;
    }

    @Override
    public InlineWidget createWidget(String id) {
        VistaAdminPublicSiteMap.Widgets widgetId;
        try {
            widgetId = VistaAdminPublicSiteMap.Widgets.valueOf(id);
        } catch (Throwable e) {
            return null;
        }
        switch (widgetId) {
        case AcceptDeclineWidget:
            return new AcceptDeclineWidget();
        case SignOutWidget:
            return new SignOutWidget();
        default:
            return null;
        }
    }
}
