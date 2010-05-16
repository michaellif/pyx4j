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
 * Created on Feb 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.preloader;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.server.SiteServicesImpl;
import com.pyx4j.site.shared.domain.Link;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.PageData;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.util.PageTypeUriEnum;
import com.pyx4j.site.shared.util.SiteFactoryUtils;

public abstract class AbstractSitesDataPreloader extends AbstractDataPreloader {

    protected int siteCount;

    protected int pageCount;

    protected int portletCount;

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(Link.class, Page.class, PageData.class, Portlet.class, Site.class);
    }

    protected String createdCounts() {
        StringBuilder b = new StringBuilder();
        b.append("Created " + siteCount + " Sites").append('\n');
        b.append("Created " + pageCount + " Pages").append('\n');
        b.append("Created " + portletCount + " Portlets");
        return b.toString();
    }

    protected Portlet createPortlet(String portletId, String caption, String html) {
        Portlet portlet = EntityFactory.create(Portlet.class);
        portlet.portletId().setValue(portletId);
        portlet.capture().setValue(caption);
        portlet.html().setValue(html);
        portletCount++;
        return portlet;
    }

    protected Site createSite(String siteId, String caption) {
        Site site = SiteFactoryUtils.createSite(siteId, caption);
        SiteServicesImpl.resetCache(siteId);
        site.footerCopiright().setValue(footerCopiright());
        siteCount++;
        return site;
    }

    protected String footerCopiright() {
        return "&copy; 2008-2010 pyx4j.com All rights reserved.";
    }

    protected Page createPage(String caption, PageTypeUriEnum pageType, String html) {
        pageCount++;
        if (html == null) {
            html = pageBodyUnderConstruction(caption);
        }
        return SiteFactoryUtils.createPage(caption, pageType, html);
    }

    protected Page createSingleWidgetPage(String caption, PageTypeUriEnum pageType, Enum<?> inlineWidget) {
        pageCount++;
        return SiteFactoryUtils.createSingleWidgetPage(caption, pageType, inlineWidget);
    }

    protected Page createSingleWidgetPage(String tabName, String caption, PageTypeUriEnum pageType, Enum<?> inlineWidget, Portlet[] leftPortlets,
            Portlet[] rightPortlets) {
        Page page = createSingleWidgetPage(caption, pageType, inlineWidget, leftPortlets, rightPortlets);
        page.tabName().setValue(tabName);
        return page;
    }

    protected Page createSingleWidgetPage(String caption, PageTypeUriEnum pageType, Enum<?> inlineWidget, Portlet[] leftPortlets, Portlet[] rightPortlets) {
        pageCount++;
        return SiteFactoryUtils.createSingleWidgetPage(caption, pageType, inlineWidget, leftPortlets, rightPortlets);
    }

    protected Page createPage(String caption, ResourceUri uri, String html) {
        pageCount++;
        if (html == null) {
            html = pageBodyUnderConstruction(caption);
        }
        return createPage(caption, uri, html, null, null, null);
    }

    protected Page createPage(String caption, PageTypeUriEnum pageType, String html, Portlet[] leftPortlets, Portlet[] rightPortlets, String[] inlineWidgets) {
        return createPage(caption, pageType.getUri(), html, leftPortlets, rightPortlets, inlineWidgets);
    }

    protected Page createPage(String tabName, String caption, PageTypeUriEnum pageType, String html, Portlet[] leftPortlets, Portlet[] rightPortlets,
            String[] inlineWidgets) {
        Page page = createPage(caption, pageType.getUri(), html, leftPortlets, rightPortlets, inlineWidgets);
        page.tabName().setValue(tabName);
        return page;
    }

    protected Page createPage(String caption, ResourceUri uri, String html, Portlet[] leftPortlets, Portlet[] rightPortlets, String[] inlineWidgets) {
        pageCount++;
        if (html == null) {
            html = pageBodyUnderConstruction(caption);
        }
        Page page = SiteFactoryUtils.createPage(caption, uri, html, leftPortlets, rightPortlets, inlineWidgets);
        return page;
    }

    protected String pageBodyUnderConstruction(String caption) {
        return "<span style='text-align:center;'><h2>'" + caption + "' page is under construction.</h2></span>";
    }
}
