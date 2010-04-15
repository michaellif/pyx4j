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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public class PagePanel extends DynamicHTML {

    private static final Logger log = LoggerFactory.getLogger(PagePanel.class);

    private final List<InlineWidget> inlineWidgets = new ArrayList<InlineWidget>();

    private final SitePanel parent;

    private final Page page;

    public PagePanel(SitePanel parent, Page page) {
        super(page.data().html().getValue(), true);
        this.parent = parent;
        this.page = page;
        setStyleName(SiteCSSClass.pyx4j_Site_PageWidget.name());
    }

    public Page getPage() {
        return page;
    }

    public void createInlineWidgets() {
        if (!page.data().inlineWidgetIds().isNull() && page.data().inlineWidgetIds().getValue().size() > 0) {
            for (String widgetId : page.data().inlineWidgetIds().getValue()) {
                //check in local (page) factory
                InlineWidget inlineWidget = null;
                //check in local (page) factory
                if (parent.getLocalWidgetFactory() != null) {
                    inlineWidget = parent.getLocalWidgetFactory().createWidget(widgetId);
                }
                //check in global factory
                if (inlineWidget == null) {
                    inlineWidget = SitePanel.getGlobalWidgetFactory().createWidget(widgetId);
                }
                InlineWidgetRootPanel root = InlineWidgetRootPanel.get(widgetId);
                if (root != null && inlineWidget != null) {
                    root.add((Widget) inlineWidget);
                    inlineWidgets.add(inlineWidget);

                    if (inlineWidget instanceof PageLeavingHandler) {
                        addPageLeavingHandler((PageLeavingHandler) inlineWidget);
                    }

                } else {
                    log.warn("Failed to add inline widget " + widgetId + " to panel.");
                }

            }
        }
    }

    public HandlerRegistration addPageLeavingHandler(PageLeavingHandler handler) {
        return addHandler(handler, PageLeavingEvent.TYPE);
    }

    public void onPageLeaving(PageLeavingEvent event) {
        this.fireEvent(event);
    }

    public void populateInlineWidgets(Map<String, String> args) {
        for (InlineWidget inlineWidget : inlineWidgets) {
            inlineWidget.populate(args);
        }
    }

    /**
     * @deprecated Remove this. Use PageLeavingHandler
     */
    @Deprecated
    public boolean onBeforeLeaving() {
        for (InlineWidget inlineWidget : inlineWidgets) {
            if (!inlineWidget.onBeforeLeaving()) {
                return false;
            }
        }
        return true;
    }
}
