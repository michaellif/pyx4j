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
 * Created on May 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client;

import com.pyx4j.examples.site.client.ExamplesWidgets.ExamplesPubWidgets;
import com.pyx4j.examples.site.client.pub.ExamplesPublicSiteResources;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.meta.SiteFactory;

public class ExamplesSiteFactory extends SiteFactory {

    private Portlet createTechnologyPortlet() {
        Portlet portlet = createPortlet("portlet-technology", "<span style='text-align:center;'><h4>Our technology</h4></span>",
                inlineWidgetHtml(ExamplesPubWidgets.pub$technologyWidget));

        portlet.inlineWidgetIds().add(ExamplesPubWidgets.pub$technologyWidget.name());
        return portlet;
    }

    public Site createPubSite() {

        Site site = createSite(ExamplesSiteMap.Sites.Pub.name(), "pyx4j.com");

        Page pageHome = createPage("Home", ExamplesSiteMap.Pub.Home.class, ExamplesPublicSiteResources.INSTANCE.pageHome().getText());
        pageHome.data().rightPortlets().add(createTechnologyPortlet());
        site.pages().add(pageHome);

        site.pages().add(
                createPage("Authentication Required", ExamplesSiteMap.Pub.Home.E530.class, ExamplesPublicSiteResources.INSTANCE.pageAuthenticationRequired()
                        .getText()));

        site.pages().add(createPage("Contact Us", ExamplesSiteMap.Pub.Home.ContactUs.class, ExamplesPublicSiteResources.INSTANCE.pageContact().getText()));

        Page pageExamples = createPage("Examples", ExamplesSiteMap.Pub.Examples.class, ExamplesPublicSiteResources.INSTANCE.pageExamples().getText());
        //pageExamples.data().leftPortlets().add(slogan1Portlet);
        site.pages().add(pageExamples);

        site.pages().add(createSingleWidgetPage("Video", ExamplesSiteMap.Pub.Examples.Video.class, ExamplesPubWidgets.pub$videoWidget));

        return site;
    }
}
