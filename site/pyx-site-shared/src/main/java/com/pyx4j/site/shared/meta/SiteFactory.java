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
 * Created on May 21, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.shared.meta;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.resources.client.ExternalTextResource;

import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.PageDataImpl;
import com.pyx4j.site.shared.domain.PageImpl;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.PortletImpl;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.domain.SiteImpl;

public class SiteFactory {

    private static final List<String> uriRegistry = new ArrayList<String>();

    protected Portlet createPortlet(String portletId, String caption, String html) {
        return createPortlet(portletId, caption, html, null, null, null);
    }

    protected Portlet createPortlet(String portletId, String caption, String html, String styleName) {
        return createPortlet(portletId, caption, html, styleName, null, null);
    }

    protected Portlet createPortlet(String portletId, String caption, String html, String actionLabel, Class<? extends NavigNode> navigNode) {
        return createPortlet(portletId, caption, html, null, actionLabel, navigNode);
    }

    protected Portlet createPortlet(String portletId, String caption, String html, String styleName, String actionLabel, Class<? extends NavigNode> navigNode) {
        return new PortletImpl(portletId, caption, html, styleName, actionLabel, navigNode);
    }

    public Site createSite(String siteId, String caption) {
        SiteImpl site = new SiteImpl(siteId, caption, footerCopyright());
        site.setLogoUrl("images/logo.png");
        return site;
    }

    protected String footerCopyright() {
        return "&copy; 2008-2010 pyx4j.com All rights reserved.";
    }

    protected static String pageBodyUnderConstruction(String caption) {
        return "<span style='text-align:center;'><h2>'" + caption + "' page is under construction.</h2></span>";
    }

    protected static String inlineWidgetHtml(Enum<?> inlineWidget) {
        return "<div id='" + inlineWidget.name() + "'></div>";
    }

    public static Page createPage(String caption, Class<? extends NavigNode> node, String html) {
        return createPage(null, caption, node, null, html, null, null, null);
    }

    protected static Page createPage(String tabName, String caption, Class<? extends NavigNode> node, String discriminator, String html,
            Portlet[] leftPortlets, Portlet[] rightPortlets, String[] inlineWidgets) {
        Page page = createPage(caption, node, discriminator, html, null, leftPortlets, rightPortlets, inlineWidgets);
        ((PageImpl) page).setTabName(tabName);
        return page;
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, Enum<?> inlineWidget) {
        return createPage(null, caption, node, null, inlineWidgetHtml(inlineWidget), null, null, new String[] { inlineWidget.name() });
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget) {
        return createPage(null, caption, node, discriminator, inlineWidgetHtml(inlineWidget), null, null, new String[] { inlineWidget.name() });
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget, String helpHtml,
            Portlet[] leftPortlets, Portlet[] rightPortlets) {
        return createSingleWidgetPage(null, caption, node, discriminator, inlineWidget, helpHtml, leftPortlets, rightPortlets);
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget,
            ExternalTextResource helpHtmlResource, Portlet[] leftPortlets, Portlet[] rightPortlets) {
        return createSingleWidgetPage(null, caption, node, discriminator, inlineWidget, helpHtmlResource, leftPortlets, rightPortlets);
    }

    public static Page createSingleWidgetPage(String tabName, String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget,
            String helpHtml, Portlet[] leftPortlets, Portlet[] rightPortlets) {
        Page page = createPage(caption, node, discriminator, inlineWidgetHtml(inlineWidget), helpHtml, leftPortlets, rightPortlets,
                new String[] { inlineWidget.name() });
        if (tabName != null) {
            ((PageImpl) page).setTabName(tabName);
        }
        return page;
    }

    public static Page createSingleWidgetPage(String tabName, String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget,
            ExternalTextResource helpHtmlResource, Portlet[] leftPortlets, Portlet[] rightPortlets) {
        Page page = createPage(caption, node, discriminator, inlineWidgetHtml(inlineWidget), null, leftPortlets, rightPortlets,
                new String[] { inlineWidget.name() });
        if (tabName != null) {
            ((PageImpl) page).setTabName(tabName);
        }
        ((PageDataImpl) page.data()).setHelpResource(helpHtmlResource);
        return page;
    }

    public static Page createPage(String caption, Class<? extends NavigNode> node, String discriminator, String html) {
        return createPage(caption, node, discriminator, html, null, null, null, null);
    }

    public static Page createPage(String caption, Class<? extends NavigNode> node, String discriminator, String html, String helpHtml, Portlet[] leftPortlets,
            Portlet[] rightPortlets, String[] inlineWidgets) {

        String uri = NavigUtils.getPageUri(node);
        if (!uriRegistry.contains(uri)) {
            uriRegistry.add(uri);
        }

        if (html == null) {
            html = caption;
        }
        PageDataImpl pageData = new PageDataImpl(html, helpHtml);
        PageImpl page = new PageImpl(caption, uri, discriminator, pageData);

        if (leftPortlets != null) {
            for (Portlet portlet : leftPortlets) {
                pageData.addLeftPortlet(portlet);
            }
        }

        if (rightPortlets != null) {
            for (Portlet portlet : rightPortlets) {
                pageData.addRightPortlet(portlet);
            }
        }

        if (inlineWidgets != null) {
            for (String widgetId : inlineWidgets) {
                pageData.addInlineWidgetId(widgetId);
            }
        }
        return page;
    }

    public static List<String> getUriRegistry() {
        return uriRegistry;
    }

}
