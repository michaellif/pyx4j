/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.LocalePanel;

public class ErrorPage extends WebPage {
    private static final long serialVersionUID = 1L;

    private VolatileTemplateResourceReference refCSS = null;

    public ErrorPage() {
        super();

        add(new BookmarkablePageLink<Void>("titleLogo", LandingPage.class));
        add(new LocalePanel("locale"));
        add(new Label("footer_legal", PMSiteContentManager.getCopyrightInfo()));

        String baseColor = PMSiteContentManager.getSiteDescriptor().baseColor().getValue();
        int styleId = PMSiteContentManager.getSiteStyle();
        String fileCSS = "error" + styleId + ".css";
        refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css", new StylesheetTemplateModel(String.valueOf(styleId)));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(refCSS);
    }
}
