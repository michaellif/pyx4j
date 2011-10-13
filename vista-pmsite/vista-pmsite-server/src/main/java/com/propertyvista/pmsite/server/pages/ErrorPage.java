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

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.http.WebRequest;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteSession;
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
        add(new Label("footer_legal", "© Starlight Apartments 2011"));

        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        refCSS = new VolatileTemplateResourceReference(TemplateResources.class, "error" + getPmsiteStyle() + ".css", "text/css", new StylesheetTemplateModel(
                baseColor));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(refCSS);
    }

    // TODO this method is a temporary plug
    protected int getPmsiteStyle() {
        Cookie pmsiteStyleCookie = null;
        List<Cookie> cookies = ((WebRequest) getRequest()).getCookies();
        if (cookies == null) {
            return 0;
        }
        for (Cookie cookie : cookies) {
            if ("pmsiteStyle".equals(cookie.getName())) {
                pmsiteStyleCookie = cookie;
                break;
            }
        }
        int styleId = 0;
        if (pmsiteStyleCookie != null) {
            styleId = Integer.valueOf(pmsiteStyleCookie.getValue());
        }
        return styleId;
    }
}
