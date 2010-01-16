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
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
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
import com.pyx4j.site.client.domain.AbstractPage;
import com.pyx4j.site.client.domain.Link;
import com.pyx4j.site.client.domain.PageUri;
import com.pyx4j.site.client.domain.Portlet;
import com.pyx4j.site.client.domain.StaticPage;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.widgets.client.style.StyleManger;

public class SitePanel extends SimplePanel implements ValueChangeHandler<String> {

    public AbstractPage homePage;

    public String logoUrl;

    public List<Link> headerLinks;

    public List<Link> footerLinks;

    public String footerCopiright;

    public List<AbstractPage> pages = new ArrayList<AbstractPage>();

    private AbsolutePanel headerPanel;

    private DockPanel mainPanel;

    private FlowPanel leftSectionPanel;

    private SimplePanel mainSectionPanel;

    private FlowPanel rightSectionPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    private Image logoImage;

    private NavigationBar primaryNavigationBar;

    private LinkBar headerLinkBar;

    private LinkBar footerLinkBar;

    private static LightTheme lightTheme = new LightTheme();

    private static DarkTheme darkTheme = new DarkTheme();

    public SitePanel() {
        setSize("100%", "100%");

        add(createContentPanel());
        setStyleName(SiteCSSClass.pyx4j_Site_SitePanel.name());

        createHeaderCaptions();

        createPrimaryNavigation();

        createLogoImage();

        createHeaderLinks();

        createFooterLinks();

        createFooterCopiright();

        StyleManger.installTheme(darkTheme);

        History.addValueChangeHandler(this);

    }

    public void show(String historyToken) {
        if (historyToken.length() > 0) {
            AbstractPage page = getPage(historyToken);
            if (page == null) {
                show(getHomePage());
            } else {
                show(page);
            }
        } else {
            show(getHomePage());
        }
    }

    private void show(AbstractPage page) {
        Widget widget = null;
        if (page instanceof StaticPage) {
            widget = new HTML(((StaticPage) page).data.html, true);
        } else if (page instanceof DynamicPage) {
            widget = ((DynamicPage) page).getWidget();
        }

        if (widget != null) {
            mainSectionPanel.setWidget(widget);
            widget.getElement().getStyle().setProperty("textAlign", "center");
        }

        setHeaderCaptions(page.caption);

        leftSectionPanel.clear();
        if (page.leftPortlets != null) {
            for (Portlet portlet : page.leftPortlets) {
                leftSectionPanel.add(createPortletWidget(portlet));
            }
        }

        rightSectionPanel.clear();
        if (page.rightPortlets != null) {
            for (Portlet portlet : page.rightPortlets) {
                rightSectionPanel.add(createPortletWidget(portlet));
            }
        }

        primaryNavigationBar.setSelected(page.uri);

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

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.setStyleName(SiteCSSClass.pyx4j_Site_Header.name());

        return headerPanel;
    }

    public void addToHeaderPanel(Widget w) {
        addToHeaderPanel(w, 0, 0);
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.remove(w);
        headerPanel.add(w, left, top);
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
        return new HtmlPortletWidget(portlet);
    }

    protected SimplePanel createMainSectionPanel() {
        SimplePanel panel = new SimplePanel();
        panel.setWidth("100%");
        return panel;
    }

    protected void createHeaderCaptions() {
        headerCaptions = new Label();
        headerCaptions.setStyleName(SiteCSSClass.pyx4j_Site_HeaderCaptions.name());

    }

    public void setHeaderCaptions(String captions) {
        headerCaptions.setText(captions);
        addToHeaderPanel(headerCaptions);
    }

    protected void createLogoImage() {
        logoImage = new Image();
        logoImage.setStyleName(SiteCSSClass.pyx4j_Site_Logo.name());

        logoImage.setUrl(logoUrl);
        addToHeaderPanel(logoImage);
        logoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (lightTheme.equals(StyleManger.getTheme())) {
                    StyleManger.installTheme(darkTheme);
                } else {
                    StyleManger.installTheme(lightTheme);
                }
            }
        });
    }

    protected void createPrimaryNavigation() {
        primaryNavigationBar = new NavigationBar(NavigationBarType.Primary);

        for (AbstractPage page : pages) {
            if (page.uri.isRoot()) {
                primaryNavigationBar.add(page.caption, page.uri);
            }
        }
        addToHeaderPanel(primaryNavigationBar);

    }

    protected void createFooterCopiright() {
        HTML html = new HTML(footerCopiright, false);
        html.setStyleName(SiteCSSClass.pyx4j_Site_FooterCopiright.name());
        addToFooterPanel(html);
    }

    protected void createHeaderLinks() {
        headerLinkBar = new LinkBar(LinkBarType.Header);

        for (Link link : headerLinks) {
            headerLinkBar.add(link);
        }
        addToHeaderPanel(headerLinkBar);
    }

    protected void createFooterLinks() {
        footerLinkBar = new LinkBar(LinkBarType.Footer);

        for (Link link : footerLinks) {
            footerLinkBar.add(link);
        }
        addToFooterPanel(footerLinkBar);
    }

    public void addPage(AbstractPage page) {
        addPage(page, false);
    }

    public void addPage(AbstractPage page, boolean isHome) {
        pages.add(page);
        if (isHome) {
            homePage = page;
        }
    }

    public AbstractPage getPage(String uri) {
        return getPage(new PageUri(uri));
    }

    public AbstractPage getPage(PageUri uri) {
        for (AbstractPage page : pages) {
            if (page.uri.equals(uri)) {
                return page;
            }
        }
        return null;
    }

    public AbstractPage getHomePage() {
        return pages.get(0);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        AbstractPage page = getPage(event.getValue());
        if (page == null) {
            show(getHomePage());
            return;
        }
        show(page);
    }

}
