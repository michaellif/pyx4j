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
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.FooterPanel;
import com.propertyvista.pmsite.server.panels.HeaderPanel;

//http://www.google.com/codesearch#ah7E8QWg9kg/trunk/src/main/java/com/jianfeiliao/portfolio/panel/content/StuffPanel.java&type=cs
public abstract class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(BasePage.class);

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

    public String getLocalizedPageTitle() {
        return null;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "main" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        response.renderCSSReference(new CssResourceReference(TemplateResources.class, "jquery-ui-1.8.16.custom.css"));
        response.renderCSSReference(new CssResourceReference(TemplateResources.class, "pmsite_jslib-1.0.css"));
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "jquery-1.6.3.min.js"));
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "jquery-ui-1.8.16.custom.min.js"));
        response.renderJavaScriptReference(new JavaScriptResourceReference(JSResources.class, "pmsite_jslib-1.0.js"));
        response.renderString("<meta name=\"gwt:property\" content=\"locale=" + ((PMSiteWebRequest) getRequest()).getSiteLocale().lang().getValue().name()
                + "\" />");
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        // add page title if not already done
        final String pageTitleId = "pageTitle";
        if (get(pageTitleId) == null) {
            String title = getLocalizedPageTitle();
            if (title != null && title.trim().length() > 0) {
                title = " - " + title;
            } else {
                title = "";
            }
            PMSiteWebRequest req = (PMSiteWebRequest) getRequest();
            add(new Label(pageTitleId, req.getContentManager().getSiteTitles(req.getSiteLocale()).residentPortalTitle().getStringView() + title));
        }

        if (ApplicationMode.isDevelopment()) {
            checkIfPageStateless(this);
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        /*
         * When RequestAdapter completes, it will store in session all stateful pages (getStatelessHint()
         * returning false). However, when ListenerInterfaceRequestHandler called, it triggers
         * setStatelessHint(false) via PageProvider constructor on any page regardless whether it has
         * stateful components or not (see ListenerInterfaceRequestHandler#respond). So, we set it back
         * to true here as CommitRequest() method of the RequestAdapter class will call Page.onDetach()
         * before checking stateless hint. See RequestAdapter#CommitRequest().
         */
        setStatelessHint(true);
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

            String message = i18n.tr("Stateful components found: ");
            if (statefulComponents.size() > 0) {
                for (Component c : statefulComponents) {
                    message += "\n" + c.getMarkupId();
                }
            }
            p.warn(message);
        }
    }

    protected void redirectOrFail(Class<? extends BasePage> pageClass, String msg) {
        if (ApplicationMode.isDevelopment()) {
            throw new RuntimeException(msg);
        } else {
            throw new RestartResponseException(pageClass);
        }
    }

}
