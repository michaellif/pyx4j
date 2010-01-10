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
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import java.util.ArrayList;

import com.pyx4j.site.client.domain.Link;
import com.pyx4j.site.client.domain.Page;
import com.pyx4j.site.client.domain.Site;

public class EasySite extends Site {

    public EasySite() {

        logoUrl = "images/logo.png";

        headerLinks = new ArrayList<Link>();
        headerLinks.add(new Link("Sign Up", "", true));
        headerLinks.add(new Link("Log In", "", true));

        footerLinks = new ArrayList<Link>();
        footerLinks.add(new Link("Technical Support", "", true));
        footerLinks.add(new Link("Privacy policy", "", true));
        footerLinks.add(new Link("Terms of Use", "", true));

        footerCopiright = "&copy; 2010 EasySite. All rights reserved.";

        {
            Page page = new Page();
            page.caption = "Home";
            page.name = "home";
            page.data.html = "Home";
            addHomePage(page);
        }

        {
            Page page = new Page();
            page.caption = "Services";
            page.name = "services";
            page.data.html = "Services";
            addPage(page);
        }

        {
            Page page = new Page();
            page.caption = "About Us";
            page.name = "aboutUs";
            page.data.html = "About Us";
            addPage(page);
        }

        {
            Page page = new Page();
            page.caption = "Contact Us";
            page.name = "contactUs";
            page.data.html = "Contact Us";
            addPage(page);
        }

    }
}
