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

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.TextTemplateResourceReference;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.panels.FooterPanel;
import com.propertyvista.pmsite.server.panels.HeaderPanel;

//http://www.google.com/codesearch#ah7E8QWg9kg/trunk/src/main/java/com/jianfeiliao/portfolio/panel/content/StuffPanel.java&type=cs
public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public BasePage() {
        this(null);

    }

    public BasePage(PageParameters parameters) {
        super(parameters);

        add(new Link<Void>("switchStyle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {

                int style = getPmsiteStyle();

                if (style == 0) {
                    style = 1;
                } else {
                    style = 0;
                }
                ((WebResponse) getResponse()).addCookie(new Cookie("pmsiteStyle", String.valueOf(style)));
                //TODO getRequestCycle().getWebResponse().addCookie(new Cookie("pmsiteStyle", String.valueOf(style)));

                setResponsePage(getPageClass(), getPageParameters());
            }
        });

        add(new HeaderPanel());
        add(new FooterPanel());

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        TextTemplateResourceReference refCSS = new TextTemplateResourceReference(TemplateResources.class, "main" + getPmsiteStyle() + ".css", "text/css",
                new StylesheetTemplateModel(baseColor));
        response.renderCSSReference(refCSS);
    }

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
        if (pmsiteStyleCookie != null) {
            return Integer.valueOf(pmsiteStyleCookie.getValue());
        } else {
            return 0;
        }
    }

}
