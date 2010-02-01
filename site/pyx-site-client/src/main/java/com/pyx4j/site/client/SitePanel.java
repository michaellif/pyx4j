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

import com.pyx4j.forms.client.gwt.DatePickerDropDownPanel;
import com.pyx4j.site.client.LinkBar.LinkBarType;
import com.pyx4j.site.client.NavigationBar.NavigationBarType;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.domain.SkinType;
import com.pyx4j.site.shared.util.ResourceUriUtil;
import com.pyx4j.widgets.client.style.StyleManger;

public abstract class SitePanel extends SimplePanel {

    private static final Logger log = LoggerFactory.getLogger(DatePickerDropDownPanel.class);

    private final Site site;

    private String siteName;

    private String siteCaption;

    private Page homePage;

    private final List<Page> pages = new ArrayList<Page>();

    private Page currentPage;

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

    private final HashMap<String, PageWidget> cahedPanels = new HashMap<String, PageWidget>();

    private static LightTheme lightTheme = new LightTheme();

    private static DarkTheme darkTheme = new DarkTheme();

    private static InlineWidgetFactory globalWidgetFactory = GWT.create(InlineWidgetFactory.class);

    public SitePanel(Site site) {
        this.site = site;
        setSize("100%", "100%");

        add(createContentPanel());
        setStyleName(SiteCSSClass.pyx4j_Site_SitePanel.name());

        createHeaderCaptionsPanel();

        createPrimaryNavigationPanel();

        createLogoImagePanel();

        createHeaderLinksPanel();

        createFooterLinksPanel();

        createFooterCopirightPanel();

        switch (site.skinType().getValue()) {
        case light:
            StyleManger.installTheme(lightTheme);
            break;
        case dark:
            StyleManger.installTheme(darkTheme);
            break;

        default:
            break;
        }

    }

    public void show(String historyToken, Map<String, String> args) {
        if (historyToken.length() > 0) {

            Page page = getPage(historyToken);
            if (page == null) {
                if (homePage != null) {
                    show(homePage);
                }
            } else {
                show(page, args);
            }
        } else {
            if (homePage != null) {
                show(homePage);
            }
        }
    }

    private void show(Page page) {
        show(page, null);
    }

    private void show(Page page, Map<String, String> args) {

        PageWidget pageWidget;
        String path = page.uri().uri().getValue();
        if (cahedPanels.containsKey(path)) {
            pageWidget = cahedPanels.get(path);
            mainSectionPanel.setWidget(pageWidget);
        } else {
            pageWidget = new PageWidget(mainSectionPanel, page.data());
            cahedPanels.put(path, pageWidget);

            if (page.data().inlineWidgetUris().size() > 0) {
                for (ResourceUri uri : page.data().inlineWidgetUris()) {
                    //check in local (page) factory
                    if (localWidgetFactory() != null) {
                        InlineWidgetRootPanel root = InlineWidgetRootPanel.get(uri);
                        InlineWidget inlineWidget = localWidgetFactory().createWidget(uri);
                        //check in global factory
                        if (inlineWidget == null) {
                            inlineWidget = globalWidgetFactory.createWidget(uri);
                        }
                        if (root != null && inlineWidget != null) {
                            inlineWidget.setStyleName(SiteCSSClass.pyx4j_Site_Content.name());
                            root.add(inlineWidget);
                            pageWidget.addInlineWidget(inlineWidget);
                        } else {
                            log.warn("Failed to add inline widget " + uri + " to panel.");
                        }
                    }
                }
            }

        }

        if (SkinType.dark.equals(site.skinType().getValue()) && lightTheme.equals(StyleManger.getTheme())) {
            StyleManger.installTheme(darkTheme);
        } else if (SkinType.light.equals(site.skinType().getValue()) && darkTheme.equals(StyleManger.getTheme())) {
            StyleManger.installTheme(lightTheme);
        }

        setHeaderCaption(page.caption().getValue());

        leftSectionPanel.clear();
        if (page.data().leftPortlets().size() > 0) {
            for (Portlet portlet : page.data().leftPortlets()) {
                leftSectionPanel.add(createPortletWidget(portlet));
            }
        }

        rightSectionPanel.clear();
        if (page.data().rightPortlets().size() > 0) {
            for (Portlet portlet : page.data().rightPortlets()) {
                rightSectionPanel.add(createPortletWidget(portlet));
            }
        }

        primaryNavigationBar.setSelected(page.uri());

        Window.setTitle(siteCaption + " " + page.caption().getValue());

        currentPage = page;

        pageWidget.executeAfterShow(args);

        executeAfterShow(args);
    }

    protected void executeAfterShow(Map<String, String> params) {
    }

    protected void showCurrent() {
        if (currentPage != null) {
            show(currentPage);
        } else {
            show(homePage);
        }
    }

    abstract protected InlineWidgetFactory localWidgetFactory();

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

    private Widget createPortletWidget(Portlet portlet) {
        return new PortletWidget(portlet);
    }

    protected SimplePanel createMainSectionPanel() {
        SimplePanel panel = new SimplePanel();
        panel.setWidth("100%");
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
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setSiteCaption(String siteCaption) {
        this.siteCaption = siteCaption;
    }

    public void setLogoImage(String logoUrl) {
        logoImage.setUrl(logoUrl);
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
            primaryNavigationBar.add(page.caption().getValue(), page.uri());
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

}
