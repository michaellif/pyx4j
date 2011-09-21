/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.resource.TextTemplateResourceReference;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.panels.GwtInclude;

public class ResidentsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public ResidentsPage() {
        super();

        add(new GwtInclude("gwtInclude"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        TextTemplateResourceReference refCSS = new TextTemplateResourceReference(TemplateResources.class, "resident" + getPmsiteStyle() + ".css", "text/css",
                new StylesheetTemplateModel(baseColor));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
