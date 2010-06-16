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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.shared.domain.DefaultSkins;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;

public class SiteFactory {

    private static final List<String> uriRegistry = new ArrayList<String>();

    protected Portlet createPortlet(String portletId, String caption, String html) {
        return createPortlet(portletId, caption, html, null);
    }

    protected Portlet createPortlet(String portletId, String caption, String html, String styleName) {
        Portlet portlet = EntityFactory.create(Portlet.class);
        portlet.portletId().setValue(portletId);
        portlet.capture().setValue(caption);
        portlet.styleName().setValue(styleName);
        portlet.html().setValue(html);
        return portlet;
    }

    public Site createSite(String siteId, String caption) {
        Site site = EntityFactory.create(Site.class);
        site.updateTimestamp().setValue(System.currentTimeMillis());
        site.siteId().setValue(siteId);
        site.siteCaption().setValue(caption);
        site.logoUrl().setValue("images/logo.png");
        site.skinType().setValue(DefaultSkins.light.name());
        site.footerCopiright().setValue(footerCopiright());
        return site;
    }

    protected String footerCopiright() {
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
        Page page = createPage(caption, node, discriminator, html, leftPortlets, rightPortlets, inlineWidgets);
        page.tabName().setValue(tabName);
        return page;
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, Enum<?> inlineWidget) {
        return createPage(null, caption, node, null, inlineWidgetHtml(inlineWidget), null, null, new String[] { inlineWidget.name() });
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget) {
        return createPage(null, caption, node, discriminator, inlineWidgetHtml(inlineWidget), null, null, new String[] { inlineWidget.name() });
    }

    public static Page createSingleWidgetPage(String caption, Class<? extends NavigNode> node, String discriminator, Enum<?> inlineWidget,
            Portlet[] leftPortlets, Portlet[] rightPortlets) {
        return createPage(caption, node, discriminator, inlineWidgetHtml(inlineWidget), leftPortlets, rightPortlets, new String[] { inlineWidget.name() });
    }

    public static Page createPage(String caption, Class<? extends NavigNode> node, String discriminator, String html) {
        return createPage(caption, node, discriminator, html, null, null, null);
    }

    public static Page createPage(String caption, Class<? extends NavigNode> node, String discriminator, String html, Portlet[] leftPortlets,
            Portlet[] rightPortlets, String[] inlineWidgets) {
        Page page = EntityFactory.create(Page.class);
        page.caption().setValue(caption);
        String uri = NavigUtils.getPageUri(node);
        if (!uriRegistry.contains(uri)) {
            uriRegistry.add(uri);
        }
        page.uri().setValue(uri);
        page.discriminator().setValue(discriminator);
        if (html == null) {
            html = caption;
        }
        page.data().html().setValue(html);

        if (leftPortlets != null) {
            for (Portlet portlet : leftPortlets) {
                page.data().leftPortlets().add(portlet);
            }
        }

        if (rightPortlets != null) {
            for (Portlet portlet : rightPortlets) {
                page.data().rightPortlets().add(portlet);
            }
        }

        if (inlineWidgets != null) {
            for (String widgetId : inlineWidgets) {
                page.data().inlineWidgetIds().add(widgetId);
            }
        }
        return page;
    }

    public static List<String> getUriRegistry() {
        return uriRegistry;
    }

}
