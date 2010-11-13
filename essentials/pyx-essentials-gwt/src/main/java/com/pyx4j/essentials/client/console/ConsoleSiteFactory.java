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

import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.meta.SiteFactory;

public class ConsoleSiteFactory extends SiteFactory {

    public static final String siteId = "console";

    public Site createSite() {
        Site site = createSite(siteId, "Pyx Console");

        site.pages().add(createSingleWidgetPage("DB Preload", ConsoleSiteMap.console.Preload.class, ConsoleSiteMap.Widgets.console$preloadWidget));

        site.pages().add(createSingleWidgetPage("Sessions", ConsoleSiteMap.console.Sessions.class, ConsoleSiteMap.Widgets.console$sessionsAdminWidget));

        site.pages().add(createSingleWidgetPage("Simulation", ConsoleSiteMap.console.Simulation.class, ConsoleSiteMap.Widgets.console$simulation));

        site.pages().add(createPage("More...", ConsoleSiteMap.console.More.class, "<div>Anything else we may need?</div>"));

        return site;
    }

}
