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

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;

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
            p.visitChildren(Component.class, new IVisitor() {

                @Override
                public void component(Object paramT, IVisit paramIVisit) {
                    if (!((Component) paramT).isStateless()) {
                        statefulComponents.add(((Component) paramT));
                    }
                }

            });

            String message = "Whoops! this page is no longer stateless";
            if (statefulComponents.size() > 0) {
                message += " - the reason is that it contains the following stateful components: ";
                for (Component c : statefulComponents) {
                    message += "\n" + c.getMarkupId();
                }
            }
            p.warn(message);
        }
    }
}
