/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
            page.data.html = "aaaaaaaaaaa aaaaaaaaaaaa aaaaaaaaaaaaa <p> aaaaaaaaaaaaa <p> aaaaaaaaaaaaa <p> aaaaaaaaaaaaa <p>";
            addPage(page);
        }
    }
}
