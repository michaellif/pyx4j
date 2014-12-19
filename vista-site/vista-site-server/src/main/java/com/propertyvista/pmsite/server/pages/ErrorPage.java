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
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.LocalePanel;

public class ErrorPage extends WebPage {
    private static final long serialVersionUID = 1L;

    private VolatileTemplateResourceReference refCSS = null;

    public ErrorPage() {
        super();

        add(new BookmarkablePageLink<Void>("titleLogo", LandingPage.class));
        add(new LocalePanel("locale"));
        add(new Label("footer_legal", ((PMSiteWebRequest) getRequest()).getContentManager().getCopyrightInfo(((PMSiteWebRequest) getRequest()).getSiteLocale())));

        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "error.css";
        refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(refCSS);
    }
}
