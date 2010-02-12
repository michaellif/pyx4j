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

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.PageData;

public class PageWidget extends HTML {

    private static final Logger log = LoggerFactory.getLogger(PageWidget.class);

    private final List<InlineWidget> inlineWidgets = new ArrayList<InlineWidget>();

    public PageWidget(SitePanel parent, PageData pageData) {
        super(pageData.html().getValue(), true);
        setStyleName(SiteCSSClass.pyx4j_Site_PageWidget.name());

        if (!pageData.inlineWidgetIds().isNull() && pageData.inlineWidgetIds().getValue().size() > 0) {
            for (String widgetId : pageData.inlineWidgetIds().getValue()) {
                //check in local (page) factory
                InlineWidget inlineWidget = null;
                InlineWidgetRootPanel root = InlineWidgetRootPanel.get(widgetId);
                //check in local (page) factory
                if (parent.getLocalWidgetFactory() != null) {
                    inlineWidget = parent.getLocalWidgetFactory().createWidget(widgetId);
                }
                //check in global factory
                if (inlineWidget == null) {
                    inlineWidget = SitePanel.getGlobalWidgetFactory().createWidget(widgetId);
                }
                if (root != null && inlineWidget != null) {
                    root.add(inlineWidget);
                    inlineWidgets.add(inlineWidget);
                } else {
                    log.warn("Failed to add inline widget " + widgetId + " to panel.");
                }

            }
        }

    }

    public void populateInlineWidgets(Map<String, String> args) {
        for (InlineWidget inlineWidget : inlineWidgets) {
            inlineWidget.populate(args);
        }

    }
}
