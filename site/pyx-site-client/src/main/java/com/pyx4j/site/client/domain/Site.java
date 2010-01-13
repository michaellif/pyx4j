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
package com.pyx4j.site.client.domain;

import java.util.ArrayList;
import java.util.List;

public class Site {

    public String homePageName;

    public String logoUrl;

    public List<Link> headerLinks;

    public List<Link> footerLinks;

    public String footerCopiright;

    public List<Page> pages = new ArrayList<Page>();

    public Site() {

    }

    public void addPage(Page page) {
        addPage(page, false);
    }

    public void addPage(Page page, boolean homePage) {
        pages.add(page);
        if (homePage) {
            homePageName = page.name;
        }
    }

    public Page getPage(String pageBreadcrumb) {
        for (Page page : pages) {
            if (page.name == pageBreadcrumb) {
                return page;
            }
        }
        return null;
    }

    public Page getHomePage() {
        return pages.get(0);
    }

}
