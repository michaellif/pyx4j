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
package com.pyx4j.examples.site.client.crm;

import com.pyx4j.examples.domain.ExamplesBehavior;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.examples.site.client.ExamplesWidgets.ExamplesCrmWidgets;
import com.pyx4j.examples.site.client.pub.ExamplesPublicSiteResources;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.site.shared.meta.NavigUtils;
import com.pyx4j.site.shared.meta.SiteFactory;

public class CrmSiteFactory extends SiteFactory {

    public Site createCrmSite() {
        Site site = createSite(NavigUtils.getSiteId(ExamplesSiteMap.Crm.class), "Pyx CRM");

        //site.pages().add(createSingleWidgetPage("Dashboard", ExamplesSiteMap.Crm.Dashboard.class, ExamplesCrmWidgets.crm$dashboardWidget));

        site.pages().add(createSingleWidgetPage("Customers", ExamplesSiteMap.Crm.Customers.class, ExamplesCrmWidgets.crm$customerListWidget));

        site.pages().add(createSingleWidgetPage("Edit Customer", ExamplesSiteMap.Crm.Customers.Edit.class, ExamplesCrmWidgets.crm$customerEditorWidget));

        site.pages().add(createSingleWidgetPage("Orders", ExamplesSiteMap.Crm.Orders.class, ExamplesCrmWidgets.crm$orderListWidget));

        site.pages().add(createSingleWidgetPage("Edit Order", ExamplesSiteMap.Crm.Orders.Edit.class, ExamplesCrmWidgets.crm$orderEditorWidget));

        if (ClientSecurityController.checkBehavior(ExamplesBehavior.CRM_ADMIN)) {

            site.pages().add(createSingleWidgetPage("Resources", ExamplesSiteMap.Crm.Resource.class, ExamplesCrmWidgets.crm$repListWidget));

            site.pages().add(createSingleWidgetPage("Edit Resource", ExamplesSiteMap.Crm.Resource.Edit.class, ExamplesCrmWidgets.crm$repEditorWidget));

            site.pages().add(createSingleWidgetPage("Users", ExamplesSiteMap.Crm.Users.class, ExamplesCrmWidgets.crm$userListWidget));

            site.pages().add(createSingleWidgetPage("Edit User", ExamplesSiteMap.Crm.Users.Edit.class, ExamplesCrmWidgets.crm$userEditorWidget));

        }

        site.pages().add(createPage("Contact Us", ExamplesSiteMap.Crm.Home.ContactUs.class, ExamplesPublicSiteResources.INSTANCE.pageContact().getText()));

        site.pages().add(
                createPage("Technical Support", ExamplesSiteMap.Crm.Home.TechnicalSupport.class, ExamplesPublicSiteResources.INSTANCE.pageContact().getText()));

        site.pages().add(createPage("Privacy policy", ExamplesSiteMap.Crm.Home.PrivacyPolicy.class, null));

        site.pages().add(createPage("Terms of Use", ExamplesSiteMap.Crm.Home.TermsOfUse.class, null));

        return site;
    }
}
