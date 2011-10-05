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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import js.JSResources;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
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

        StatelessLink<Void> switchStyleLink = new StatelessLink<Void>("switchStyle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {

                int style = getPmsiteStyle();

                if (style == 0) {
                    style = 1;
                } else {
                    style = 0;
                }

                Cookie cookie = new Cookie("pmsiteStyle", String.valueOf(style));
                cookie.setPath("/");

                ((WebResponse) getResponse()).addCookie(cookie);

                setResponsePage(getPageClass(), getPageParameters());
            }

        };
        add(switchStyleLink);

        add(new HeaderPanel());
        add(new FooterPanel());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String baseColor = ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().baseColor().getValue();
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, "main" + getPmsiteStyle() + ".css",
                "text/css", new StylesheetTemplateModel(baseColor));
        response.renderCSSReference(refCSS);
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "jquery-1.6.3.min.js"));
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "pmsite_jslib-1.0.js"));
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
        int styleId = 0;
        if (pmsiteStyleCookie != null) {
            styleId = Integer.valueOf(pmsiteStyleCookie.getValue());
        }
        return styleId;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        if (ApplicationMode.isDevelopment()) {
            checkIfPageStateless(this);
        }
    }

    private void checkIfPageStateless(Page p) {
        if (!p.isPageStateless()) {
            // find out why
            final List<Component> statefulComponents = new ArrayList<Component>();
            p.visitChildren(Component.class, new IVisitor<Component, Object>() {

                @Override
                public void component(Component paramT, IVisit<Object> paramIVisit) {
                    if (!paramT.isStateless()) {
                        statefulComponents.add(paramT);
                    }
                }

            });

            String message = "Stateful components found: ";
            if (statefulComponents.size() > 0) {
                for (Component c : statefulComponents) {
                    message += "\n" + c.getMarkupId();
                }
            }
            p.warn(message);
        }
    }
}
