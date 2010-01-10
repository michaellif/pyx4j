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

import com.pyx4j.site.client.domain.Page;
import com.pyx4j.site.client.domain.Site;

public class EasySite extends Site {

    public EasySite() {

        logoUrl = "images/logo.png";

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
