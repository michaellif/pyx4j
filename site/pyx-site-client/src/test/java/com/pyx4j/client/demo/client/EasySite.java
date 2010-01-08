/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
