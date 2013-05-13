/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.CssResourceReference;

import templates.TemplateResources;

import com.pyx4j.essentials.server.admin.SystemMaintenance;

public class MaintenancePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public MaintenancePage() {
        super();

        add(new Label("message", SystemMaintenance.getApplicationMaintenanceMessage()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(new CssResourceReference(TemplateResources.class, "common/maintenance.css"));
    }
}