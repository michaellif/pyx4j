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
 * Created on Mar 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client.console;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.domain.SkinType;

public class ConsoleSiteFactory {

    public static final String siteId = "console";

    public enum Widgets {

        console$preloadWidget

    }

    public static Site createSite() {
        Site site = EntityFactory.create(Site.class);
        site.siteId().setValue(siteId);
        site.siteCaption().setValue("Pyx Console");
        site.skinType().setValue(SkinType.light);

        site.pages().add(createWidgetPage("DB Preload", ConsolePageType.console$preload, Widgets.console$preloadWidget));

        site.pages().add(createPage("More...", ConsolePageType.console$more, "<div>Anything else we may need?</div>"));

        return site;
    }

    private static Page createPage(String caption, ConsolePageType pageType, String html) {
        Page page = EntityFactory.create(Page.class);
        page.caption().setValue(caption);
        page.uri().set(pageType.getUri());
        page.data().html().setValue(html);
        return page;
    }

    private static Page createWidgetPage(String caption, ConsolePageType pageType, Widgets widget) {
        Page p = createPage(caption, pageType, "<div id='" + widget.name() + "'/>");
        p.data().inlineWidgetIds().add(widget.name());
        return p;
    }
}
