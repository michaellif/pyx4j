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
 * Created on Jan 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public class PagePanel extends ContentPanel {

    private static final Logger log = LoggerFactory.getLogger(PagePanel.class);

    private final PageContainer container;

    private final Page page;

    public PagePanel(SitePanel parent, Page page, ClientBundleWithLookup bundle) {
        super(parent);
        container = new PageContainer(page.data().html(), bundle, true);
        this.page = page;
        container.setStyleName(SiteCSSClass.pyx4j_Site_PagePanel.name());
        setWidget(container);
    }

    public Page getPage() {
        return page;
    }

    public void createInlineWidgets(AsyncCallback<Void> widgetsAvalable) {
        createInlineWidgets(page.data().inlineWidgetIds(), widgetsAvalable);
    }

    @Override
    protected void injectInlineWidget(String widgetId, InlineWidget inlineWidget) {
        NodeList<Element> htmlElements = container.getElement().getElementsByTagName("div");
        boolean replaced = false;
        if (htmlElements != null) {
            for (int i = 0; i < htmlElements.getLength(); i++) {
                if (widgetId.endsWith(htmlElements.getItem(i).getId())) {
                    DivElement el = DivElement.as(htmlElements.getItem(i));
                    InlineWidgetRootPanel root = new InlineWidgetRootPanel(el, true);
                    root.add((Widget) inlineWidget);
                    addInlineWidget(inlineWidget);
                    if (inlineWidget instanceof PageLeavingHandler) {
                        container.addPageLeavingHandler((PageLeavingHandler) inlineWidget);
                    }
                    container.addInlineWidget(root);
                    replaced = true;
                    break;
                }
            }
        }
        if (!replaced) {
            log.warn("Failed to add inline widget {} to panel {}.", widgetId, page.caption());
        }
    }

    public void onPageLeaving(PageLeavingEvent event) {
        container.fireEvent(event);
    }

    @Override
    protected void onLoad() {
        log.debug("PagePanel [{}] onLoad", page.caption());
        super.onLoad();
    }

    @Override
    protected void onUnload() {
        log.debug("PagePanel [{}] onUnload", page.caption());
        super.onUnload();
    }

    class PageContainer extends DynamicHTML {

        public PageContainer(String html, ClientBundleWithLookup bundle, boolean wordWrap) {
            super(html, bundle, wordWrap);
        }

        void addInlineWidget(InlineWidgetRootPanel widget) {
            getChildren().add(widget);
            adopt(widget);

        }

        public HandlerRegistration addPageLeavingHandler(PageLeavingHandler handler) {
            return container.addHandler(handler, PageLeavingEvent.TYPE);
        }

    }

}
