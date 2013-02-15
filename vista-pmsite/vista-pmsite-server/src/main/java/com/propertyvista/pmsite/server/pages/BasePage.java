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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.ContainerInfo;
import org.apache.wicket.markup.DefaultMarkupCacheKeyProvider;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCache;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.FooterPanel;
import com.propertyvista.pmsite.server.panels.HeaderPanel;
import com.propertyvista.portal.rpc.DeploymentConsts;

//http://www.google.com/codesearch#ah7E8QWg9kg/trunk/src/main/java/com/jianfeiliao/portfolio/panel/content/StuffPanel.java&type=cs
public abstract class BasePage extends WebPage implements IMarkupResourceStreamProvider {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(BasePage.class);

    public static final String META_TITLE = "pageTitle";

    public static final String META_DESCRIPTION = "pageDescription";

    public static final String META_KEYWORDS = "pageKeywords";

    public static final String RESIDENT_CUSTOM_CONTENT_PANEL = "contentPanel";

    public static final String RESIDENT_LOGIN_PANEL = "loginPanel";

    public static class LocalizedHtmlTag extends TransparentWebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public LocalizedHtmlTag(String id) {
            super(id);
            String lang = ((PMSiteWebRequest) getRequest()).getSiteLocale().lang().getValue().name();
            add(AttributeModifier.replace("lang", lang));
        }
    }

    private final PMSiteContentManager cm;

    private final AvailableLocale locale;

    public BasePage() {
        this(null);
    }

    public BasePage(PageParameters parameters) {
        super(parameters);

        cm = ((PMSiteWebRequest) getRequest()).getContentManager();
        locale = ((PMSiteWebRequest) getRequest()).getSiteLocale();

        boolean residentOnly = cm.isResidentOnlyMode();

        if (residentOnly && !(this instanceof ResidentsPage) && !(this instanceof StaticPage)) {
            // render residents page
            getRequestCycle().replaceAllRequestHandlers(
                    new RenderPageRequestHandler(new PageProvider(ResidentsPage.class), RenderPageRequestHandler.RedirectPolicy.AUTO_REDIRECT));
        } else if (!cm.isCustomResidentsContentEnabled()) {
            // default view
            add(new LocalizedHtmlTag("localizedHtml"));
            add(new HeaderPanel(residentOnly));
            add(new FooterPanel(residentOnly));
        }
    }

    public PMSiteContentManager getCM() {
        return cm;
    }

    public AvailableLocale getAvailableLocale() {
        return locale;
    }

    public String getLocalizedPageTitle() {
        return null;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (!cm.isCustomResidentsContentEnabled()) {
            String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
            String fileCSS = skin + "/" + "main.css";
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
    }

    @Override
    protected void onBeforeRender() {
        // If page has a form, the FormListener will be called on submit and will
        // set the page stateless hint to false if page is to be rendered, which
        // in turn will trigger Session.bind() in Page.onBeforeRender(). So, we
        // set it back to true here to block session bind()
        setStatelessHint(true);

        super.onBeforeRender();

        if (!cm.isCustomResidentsContentEnabled()) {
            // add page title if not already done
            if (get(META_TITLE) == null) {
                String title = getLocalizedPageTitle();
                if (title != null && title.trim().length() > 0) {
                    title = " - " + title;
                } else {
                    title = "";
                }
                PMSiteWebRequest req = (PMSiteWebRequest) getRequest();
                add(new Label(META_TITLE, req.getContentManager().getSiteTitles(req.getSiteLocale()).residentPortalTitle().getStringView() + title));
            }
            if (get(META_DESCRIPTION) == null) {
                add(new Label(META_DESCRIPTION));
            }
            if (get(META_KEYWORDS) == null) {
                add(new Label(META_KEYWORDS));
            }
        }
        if (ApplicationMode.isDevelopment()) {
            checkIfPageStateless(this);
        }
    }

    @Override
    protected void onDetach() {
        /*
         * When RequestAdapter completes, it will store in session all stateful pages (getStatelessHint()
         * returning false). However, when ListenerInterfaceRequestHandler called, it triggers
         * setStatelessHint(false) via PageProvider constructor on any page regardless whether it has
         * stateful components or not (see ListenerInterfaceRequestHandler#respond). So, we set it back
         * to true here as CommitRequest() method of the RequestAdapter class will call Page.onDetach()
         * before checking stateless hint. See RequestAdapter#CommitRequest().
         */
        setStatelessHint(true);
        super.onDetach();
    }

    @Override
    public Markup getAssociatedMarkup() {
        if (getCM().isSiteUpdated()) {
            // remove old markup from cache
            IMarkupCache cache = MarkupFactory.get().getMarkupCache();
            String cacheKey = new DefaultMarkupCacheKeyProvider().getCacheKey(this, getClass());
            cache.removeMarkup(cacheKey);
        }
        return super.getAssociatedMarkup();
    }

    @Override
    public IResourceStream getMarkupResourceStream(final MarkupContainer container, Class<?> containerClass) {
        PMSiteContentManager cm = getCM();
        if (this instanceof ResidentsPage && cm != null && cm.isCustomResidentsContentEnabled()) {
            String content = cm.getCustomResidentsContent(((PMSiteWebRequest) getRequest()).getSiteLocale());
            content = content.replaceFirst(DeploymentConsts.RESIDENT_CONTENT_ID, "wicket:id=\"" + RESIDENT_CUSTOM_CONTENT_PANEL + "\"");
            content = content.replaceFirst(DeploymentConsts.RESIDENT_LOGIN_ID, "wicket:id=\"" + RESIDENT_LOGIN_PANEL + "\"");
            return new MarkupResourceStream(new StringResourceStream(content), new ContainerInfo(this), getClass());
        } else {
            return new DefaultMarkupResourceStreamProvider().getMarkupResourceStream(container, containerClass);
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

    protected void redirectOrFail(Class<? extends BasePage> pageClass, String msg) {
        if (ApplicationMode.isDevelopment()) {
            throw new RuntimeException(msg);
        } else {
            throw new RestartResponseException(pageClass);
        }
    }
}
