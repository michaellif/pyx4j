/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.gwt.commons.AsyncCallbackAggregator;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.LinkBar.LinkBarType;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.NavigUtils;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.style.StyleManger;

public abstract class SitePanel extends SimplePanel {

    private static final Logger log = LoggerFactory.getLogger(SitePanel.class);

    private final Site site;

    private Page homePage;

    private final Map<String, List<Page>> pages = new HashMap<String, List<Page>>();

    private PagePanel currentPagePanel;

    private AbsolutePanel headerPanel;

    private DockPanel mainPanel;

    private FlowPanel leftSectionPanel;

    private SimplePanel mainSectionPanel;

    private FlowPanel rightSectionPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    protected Image logoImage;

    private String logoImageLink;

    private HTML copyrightHtml;

    private NavigationBar primaryNavigationBar;

    private LinkBar headerLinkBar;

    private LinkBar footerLinkBar;

    private final HashMap<String, PagePanel> cachedPanels = new HashMap<String, PagePanel>();

    private static InlineWidgetFactory globalWidgetFactory = GWT.create(InlineWidgetFactoryGlobal.class);

    private SkinFactory skinFactory;

    private final ClientBundleWithLookup bundle;

    protected HandlerRegistrationGC hrgc = new HandlerRegistrationGC();

    public SitePanel(Site site, ClientBundleWithLookup bundle) {
        this.site = site;
        this.bundle = bundle;
        setSize("100%", "100%");

        setWidget(createContentPanel());
        setStyleName(SiteCSSClass.pyx4j_Site_SitePanel.name());

        createHeaderCaptionsPanel();

        createPrimaryNavigationPanel();

        createLogoImagePanel();

        createHeaderLinksPanel();

        createFooterLinksPanel();

        createFooterCopyrightPanel();

        setLogoImage(site.logoUrl().getValue());
        setFooterCopyright(site.footerCopyright().getValue());

        {
            for (Page page : site.pages()) {
                addPage(page);
            }
            homePage = site.pages().get(0);
        }
    }

    public Site getSite() {
        return site;
    }

    public void show(String historyToken, Map<String, String> args) {
        if (historyToken.length() > 0) {

            Page page = getPage(historyToken);
            if (page == null) {
                if (homePage != null) {
                    show(homePage, null);
                }
            } else {
                show(page, args);
            }
        } else {
            if (homePage != null) {
                show(homePage, null);
            }
        }
    }

    protected void show(final Page page, final Map<String, String> args) {

        log.info("Show page " + page.uri().getStringView());

        StyleManger.installTheme(skinFactory.createSkin(site.skinType().getValue()));

        final String key = page.uri().getValue() + "$" + page.discriminator();
        if (cachedPanels.containsKey(key)) {
            currentPagePanel = cachedPanels.get(key);
            mainSectionPanel.setWidget(currentPagePanel);
            showAsyncContinue(page, args);
        } else {
            final PagePanel newPagePanel = new PagePanel(this, page, bundle);
            newPagePanel.createInlineWidgets(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }

                @Override
                public void onSuccess(Void result) {
                    currentPagePanel = newPagePanel;
                    cachedPanels.put(key, currentPagePanel);
                    mainSectionPanel.setWidget(currentPagePanel);
                    showAsyncContinue(page, args);
                }
            });
        }

    }

    private void showAsyncContinue(Page page, final Map<String, String> args) {
        Window.setTitle(page.caption().getValue() + " | " + site.siteCaption().getValue());
        Window.scrollTo(0, 0);

        setHeaderCaption(page.caption().getValue());

        primaryNavigationBar.setSelected(page.uri().getValue());

        final AsyncCallbackAggregator callback = new AsyncCallbackAggregator(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(Void result) {
                currentPagePanel.populateInlineWidgets(args);
            }
        });
        callback.expect();
        initPortlets(args, leftSectionPanel, page.data().leftPortlets(), callback);
        initPortlets(args, rightSectionPanel, page.data().rightPortlets(), callback);
        callback.onSuccess(null);
    }

    private void initPortlets(final Map<String, String> args, FlowPanel sectionPanel, IList<Portlet> portlets, AsyncCallbackAggregator callback) {
        sectionPanel.clear();
        for (Portlet portlet : portlets) {
            if (isPortletVisible(portlet)) {
                PortletPanel portletPanel = new PortletPanel(this, portlet, bundle);
                sectionPanel.add(portletPanel);
                callback.expect();
                portletPanel.createInlineWidgets(callback);
                portletPanel.populateInlineWidgets(args);
            }
        }
    }

    @Override
    protected void onUnload() {
        hrgc.removeHandlers();
    }

    public PagePanel getCurrentPagePanel() {
        return currentPagePanel;
    }

    public void onPageLeaving(PageLeavingEvent event) {
        if (currentPagePanel != null) {
            currentPagePanel.onPageLeaving(event);
        }
    }

    public static InlineWidgetFactory getGlobalWidgetFactory() {
        return globalWidgetFactory;
    }

    public abstract InlineWidgetFactory getLocalWidgetFactory();

    public AsyncInlineWidgetFactory getAsyncInlineWidgetFactory() {
        return null;
    }

    protected Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");

        contentPanel.setStyleName(SiteCSSClass.pyx4j_Site_ContentPanel.name());

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);

        mainPanel = createMainPanel();
        contentPanel.add(mainPanel);

        leftSectionPanel = createLeftSectionPanel();
        mainPanel.add(leftSectionPanel, DockPanel.WEST);

        mainSectionPanel = createMainSectionPanel();
        mainPanel.add(mainSectionPanel, DockPanel.CENTER);
        mainPanel.setCellWidth(mainSectionPanel, "100%");
        mainPanel.setCellHeight(mainSectionPanel, "100%");

        rightSectionPanel = createRightSectionPanel();
        mainPanel.add(rightSectionPanel, DockPanel.EAST);

        footerPanel = createFooterPanel();
        contentPanel.add(footerPanel);

        return contentPanel;
    }

    public void addToHeaderPanel(Widget w) {
        addToHeaderPanel(w, 0, 0);
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.remove(w);
        headerPanel.add(w, left, top);
    }

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.setStyleName(SiteCSSClass.pyx4j_Site_Header.name());

        return headerPanel;
    }

    protected AbsolutePanel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();

        footerPanel.setStyleName(SiteCSSClass.pyx4j_Site_Footer.name());

        return footerPanel;
    }

    public void addToFooterPanel(Widget w) {
        addToFooterPanel(w, 0, 0);
    }

    public void addToFooterPanel(Widget w, int left, int top) {
        footerPanel.add(w, left, top);
    }

    protected DockPanel createMainPanel() {
        DockPanel mainPanel = new DockPanel();

        Style style = mainPanel.getElement().getStyle();

        style.setProperty("width", "100%");

        mainPanel.setStyleName(SiteCSSClass.pyx4j_Site_MainPanel.name());

        return mainPanel;
    }

    protected FlowPanel createLeftSectionPanel() {
        FlowPanel panel = new FlowPanel();
        return panel;
    }

    protected FlowPanel createRightSectionPanel() {
        FlowPanel panel = new FlowPanel();
        return panel;
    }

    protected SimplePanel createMainSectionPanel() {
        SimplePanel panel = new SimplePanel();
        panel.setWidth("100%");
        panel.setStyleName(SiteCSSClass.pyx4j_Site_MainSectionPanel.name());
        return panel;
    }

    protected void createHeaderCaptionsPanel() {
        headerCaptions = new Label();
        headerCaptions.setStyleName(SiteCSSClass.pyx4j_Site_HeaderCaptions.name());

    }

    public void setHeaderCaption(String captions) {
        headerCaptions.setText(captions);
        addToHeaderPanel(headerCaptions);
    }

    protected void createLogoImagePanel() {
        logoImage = new Image();
        logoImage.setStyleName(SiteCSSClass.pyx4j_Site_Logo.name());

        addToHeaderPanel(logoImage);
        logoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                logoImageOnClick();
            }
        });
    }

    protected void logoImageOnClick() {
        if (logoImageLink != null) {
            AbstractSiteDispatcher.show(logoImageLink);
        }
    }

    public String getSiteName() {
        return site.siteId().getValue();
    }

    public void setLogoImage(String logoUrl) {
        if (logoUrl != null) {
            logoImage.setVisible(true);
            logoImage.setUrl(logoUrl);
        } else {
            logoImage.setVisible(false);
        }
    }

    public void setLogoImageLink(Class<? extends NavigNode> page, String title) {
        setLogoImageLink(NavigUtils.getPageUri(page), title);
    }

    public void setLogoImageLink(String path, String title) {
        logoImageLink = path;
        if (logoImageLink != null) {
            logoImage.getElement().getStyle().setCursor(Cursor.POINTER);
            logoImage.setTitle(title);
        }
    }

    protected void createPrimaryNavigationPanel() {
        primaryNavigationBar = new NavigationBar();
        addToHeaderPanel(primaryNavigationBar);

    }

    protected void createFooterCopyrightPanel() {
        copyrightHtml = new HTML("", false);
        copyrightHtml.setStyleName(SiteCSSClass.pyx4j_Site_FooterCopyright.name());
        addToFooterPanel(copyrightHtml);
    }

    public void setFooterCopyright(String html) {
        copyrightHtml.setHTML(html);
    }

    protected void createHeaderLinksPanel() {
        headerLinkBar = new LinkBar(LinkBarType.Header);
        addToHeaderPanel(headerLinkBar);
    }

    public void addHeaderLink(LinkBarItem link, boolean separator) {
        headerLinkBar.add(link, separator);
    }

    public void removeHeaderLink(LinkBarItem link) {
        headerLinkBar.remove(link);
    }

    public void setVisibleHeaderLink(LinkBarItem link, boolean flag) {
        headerLinkBar.setVisible(link, flag);
    }

    protected void createFooterLinksPanel() {
        footerLinkBar = new LinkBar(LinkBarType.Footer);
        addToFooterPanel(footerLinkBar);
    }

    public void addFooterLink(LinkBarItem link, boolean separator) {
        footerLinkBar.add(link, separator);
    }

    private void addPage(Page page) {
        List<Page> sameUriPages = pages.get(page.uri().getValue());
        if (sameUriPages == null) {
            sameUriPages = new ArrayList<Page>();
            pages.put(page.uri().getValue(), sameUriPages);
            if (NavigUtils.isRoot(page.uri().getValue())) {
                if (page.tabName().getValue() == null) {
                    primaryNavigationBar.add(page.caption().getValue(), page.uri().getValue());
                } else {
                    primaryNavigationBar.add(page.tabName().getValue(), page.uri().getValue());
                }
            }
        }
        sameUriPages.add(page);

    }

    public Page getPage(String uri) {
        List<Page> sameUriPages = pages.get(uri);
        if (sameUriPages != null && sameUriPages.size() > 0) {
            return selectPageForGivenUri(sameUriPages);
        } else {
            return null;
        }
    }

    protected Page selectPageForGivenUri(List<Page> sameUriPages) {
        return sameUriPages.get(0);
    }

    protected boolean isPortletVisible(Portlet portlet) {
        return true;
    }

    public Page getHomePage() {
        return homePage;
    }

    public void onAfterLogIn() {

    }

    public void onAfterLogOut() {

    }

    public SkinFactory getSkinFactory() {
        return skinFactory;
    }

    public void setSkinFactory(SkinFactory skinFactory) {
        this.skinFactory = skinFactory;
    }

}
