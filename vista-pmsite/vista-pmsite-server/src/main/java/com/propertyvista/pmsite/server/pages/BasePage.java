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

import js.JSResources;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.FooterPanel;
import com.propertyvista.pmsite.server.panels.HeaderPanel;

//http://www.google.com/codesearch#ah7E8QWg9kg/trunk/src/main/java/com/jianfeiliao/portfolio/panel/content/StuffPanel.java&type=cs
public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public static class LocalizedHtmlTag extends TransparentWebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public LocalizedHtmlTag(String id) {
            super(id);
            String lang = ((PMSiteWebRequest) getRequest()).getSiteLocale().lang().getValue().name();
            add(AttributeModifier.replace("lang", lang));
        }
    }

    public BasePage() {
        this(null);

    }

    public BasePage(PageParameters parameters) {
        super(parameters);
        add(new LocalizedHtmlTag("localizedHtml"));
        add(new HeaderPanel());
        add(new FooterPanel());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "main" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                new StylesheetTemplateModel(getRequest()));
        response.renderCSSReference(refCSS);
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "jquery-1.6.3.min.js"));
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "pmsite_jslib-1.0.js"));
        response.renderString("<meta name=\"gwt:property\" content=\"locale=" + ((PMSiteWebRequest) getRequest()).getSiteLocale().lang().getValue().name()
                + "\" />");
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
