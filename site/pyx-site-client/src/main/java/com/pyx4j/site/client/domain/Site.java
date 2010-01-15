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

    public AbstractPage homePage;

    public String logoUrl;

    public List<Link> headerLinks;

    public List<Link> footerLinks;

    public String footerCopiright;

    public List<AbstractPage> pages = new ArrayList<AbstractPage>();

    public Site() {

    }

    public void addPage(AbstractPage page) {
        addPage(page, false);
    }

    public void addPage(AbstractPage page, boolean isHome) {
        pages.add(page);
        if (isHome) {
            homePage = page;
        }
    }

    public AbstractPage getPage(String uri) {
        return getPage(new PageUri(uri));
    }

    public AbstractPage getPage(PageUri uri) {
        for (AbstractPage page : pages) {
            if (page.uri.equals(uri)) {
                return page;
            }
        }
        return null;
    }

    public AbstractPage getHomePage() {
        return pages.get(0);
    }

}
