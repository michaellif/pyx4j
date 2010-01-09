/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class Site extends PageContainer {

    public String homePageName;

    public String logoUrl;

    public Site() {

    }

    public void addHomePage(Page page) {
        homePageName = page.name;
        addPage(page);
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
