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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.pyx4j.site.client.LinkBar.LinkBarType;
import com.pyx4j.site.client.NavigationBar.NavigationBarType;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.util.ResourceUriUtil;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.style.StyleManger;

public abstract class SitePanel extends SimplePanel {

    private static final Logger log = LoggerFactory.getLogger(SitePanel.class);

    private final Site site;

    private Page homePage;

    private final List<Page> pages = new ArrayList<Page>();

    private PagePanel currentPagePanel;

    private AbsolutePanel headerPanel;

    private DockPanel mainPanel;

    private FlowPanel leftSectionPanel;

    private SimplePanel mainSectionPanel;

    private FlowPanel rightSectionPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    private Image logoImage;

    private HTML copitightHtml;

    private NavigationBar primaryNavigationBar;

    private LinkBar headerLinkBar;

    private LinkBar footerLinkBar;

    private final HashMap<String, PagePanel> cachedPanels = new HashMap<String, PagePanel>();

    private static InlineWidgetFactory globalWidgetFactory = GWT.create(InlineWidgetFactoryGlobal.class);

    private SkinFactory skinFactory;

    public SitePanel(Site site) {
        this.site = site;
        setSize("100%", "100%");

        setWidget(createContentPanel());
        setStyleName(SiteCSSClass.pyx4j_Site_SitePanel.name());

        createHeaderCaptionsPanel();

        createPrimaryNavigationPanel();

        createLogoImagePanel();

        createHeaderLinksPanel();

        createFooterLinksPanel();

        createFooterCopirightPanel();

        setLogoImage(site.logoUrl().getValue());
        setFooterCopiright(site.footerCopiright().getValue());

        {
            boolean isHome = true;
            for (Page page : site.pages()) {
                addPage(page, isHome);
                isHome = false;
            }
        }

        skinFactory = new DefaultSkinFactory();
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

    protected void show(Page page, Map<String, String> args) {

        String path = page.uri().uri().getValue();
        if (cachedPanels.containsKey(path)) {
            currentPagePanel = cachedPanels.get(path);
            mainSectionPanel.setWidget(currentPagePanel);
        } else {
            currentPagePanel = new PagePanel(this, page);
            cachedPanels.put(path, currentPagePanel);
            mainSectionPanel.setWidget(currentPagePanel);
            currentPagePanel.createInlineWidgets();
        }

        StyleManger.installTheme(skinFactory.createSkin(site.skinType().getValue()));

        setHeaderCaption(page.caption().getValue());

        leftSectionPanel.clear();
        if (page.data().leftPortlets().size() > 0) {
            for (Portlet portlet : page.data().leftPortlets()) {
                PortletPanel portletPanel = new PortletPanel(this, portlet);
                leftSectionPanel.add(portletPanel);
                portletPanel.createInlineWidgets();
                portletPanel.populateInlineWidgets(args);
            }
        }

        rightSectionPanel.clear();
        if (page.data().rightPortlets().size() > 0) {
            for (Portlet portlet : page.data().rightPortlets()) {
                PortletPanel portletPanel = new PortletPanel(this, portlet);
                rightSectionPanel.add(portletPanel);
                portletPanel.createInlineWidgets();
                portletPanel.populateInlineWidgets(args);
            }
        }

        primaryNavigationBar.setSelected(page.uri());

        Window.setTitle(page.caption().getValue() + " | " + site.siteCaption().getValue());

        currentPagePanel.populateInlineWidgets(args);

    }

    public PagePanel getCurrentPagePanel() {
        return currentPagePanel;
    }

    public void onPageLeaving(PageLeavingEvent event) {
        if (currentPagePanel != null) {
            currentPagePanel.onPageLeaving(event);
        }
    }

    /**
     * @deprecated Remove this. Use PageLeavingHandler
     */
    @Deprecated
    public boolean onBeforeLeaving() {
        if (currentPagePanel != null && !currentPagePanel.onBeforeLeaving()) {
            return false;
        }
        return true;
    }

    public static InlineWidgetFactory getGlobalWidgetFactory() {
        return globalWidgetFactory;
    }

    public abstract InlineWidgetFactory getLocalWidgetFactory();

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
        logoImage.getElement().getStyle().setCursor(Cursor.POINTER);

        addToHeaderPanel(logoImage);
        logoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.Location.replace("/");
            }
        });
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

    protected void createPrimaryNavigationPanel() {
        primaryNavigationBar = new NavigationBar(NavigationBarType.Primary);
        addToHeaderPanel(primaryNavigationBar);

    }

    protected void createFooterCopirightPanel() {
        copitightHtml = new HTML("", false);
        copitightHtml.setStyleName(SiteCSSClass.pyx4j_Site_FooterCopiright.name());
        addToFooterPanel(copitightHtml);
    }

    public void setFooterCopiright(String html) {
        copitightHtml.setHTML(html);
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

    public void addPage(com.pyx4j.site.shared.domain.Page page, boolean isHome) {
        pages.add(page);
        if (isHome) {
            homePage = page;
        }
        if (ResourceUriUtil.isRoot(page.uri())) {
            if (page.tabName().getValue() == null) {
                primaryNavigationBar.add(page.caption().getValue(), page.uri());
            } else {
                primaryNavigationBar.add(page.tabName().getValue(), page.uri());
            }
        }
    }

    public Page getPage(String uri) {
        for (Page page : pages) {
            if (page.uri().uri().getValue().equals(uri)) {
                return page;
            }
        }
        return null;
    }

    public Page getPage(ResourceUri uri) {
        for (Page page : pages) {
            if (page.uri().getValue().equals(uri.getValue())) {
                return page;
            }
        }
        return null;
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
